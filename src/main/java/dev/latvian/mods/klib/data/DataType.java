package dev.latvian.mods.klib.data;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.command.EnumDataTypeArgument;
import dev.latvian.mods.klib.command.ParsedDataTypeArgument;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.util.Cast;
import dev.latvian.mods.klib.util.NameProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public record DataType<T>(
	Codec<T> codec,
	StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec,
	List<Map.Entry<String, T>> enumValues,
	@Nullable DataType<?> componentType
) implements CustomRegistryValue<ByteBuf, DataType<?>>, ArgumentGetter<T> {
	public static final CustomRegistry<ByteBuf, DataType<?>> REGISTRY = CustomRegistry.create("data_type");

	public static final Codec<Ref<DataType<?>>> CODEC = REGISTRY.codec();
	public static final StreamCodec<ByteBuf, Ref<DataType<?>>> STREAM_CODEC = REGISTRY.streamCodec();
	public static final DataType<Ref<DataType<?>>> DATA_TYPE = REGISTRY.dataType();

	public static <T> DataType<T> of(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, List<Map.Entry<String, T>> enumValues) {
		return new DataType<>(codec, streamCodec, enumValues, null);
	}

	public static <T> DataType<T> of(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
		return of(codec, streamCodec, List.of());
	}

	public static <E> DataType<E> of(E[] values, @Nullable NameProvider<E> nameProvider) {
		return of(KLibCodecs.anyEnum(values, nameProvider), KLibStreamCodecs.anyEnum(values), NameProvider.resolve(nameProvider).toEntryList(values));
	}

	public static <E> DataType<E> of(E[] values) {
		return of(values, null);
	}

	public static <T> DataType<ResourceKey<T>> of(ResourceKey<? extends Registry<T>> registry) {
		return of(ResourceKey.codec(registry), KLibStreamCodecs.resourceKey(registry));
	}

	public static <C, T extends C> DataType<C> unit(T value) {
		return of(MapCodec.unitCodec(value), StreamCodec.unit(value), List.of(Map.entry("default", value)));
	}

	public static <L, R> DataType<Either<L, R>> either(DataType<L> left, DataType<R> right) {
		return of(
			Codec.either(left.codec(), right.codec()),
			ByteBufCodecs.either(left.streamCodec(), right.streamCodec())
		);
	}

	public static <T, L, R> DataType<T> either(DataType<L> left, DataType<R> right, Function<L, T> leftTo, Function<R, T> rightTo, Function<T, Either<L, R>> from) {
		Function<Either<L, R>, T> to = e -> e.map(leftTo, rightTo);

		return of(
			Codec.either(left.codec(), right.codec()).xmap(to, from),
			ByteBufCodecs.either(left.streamCodec(), right.streamCodec()).map(to, from)
		);
	}

	public DataType(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, List<Map.Entry<String, T>> enumValues, @Nullable DataType<?> componentType) {
		this.codec = codec;
		this.streamCodec = streamCodec;
		this.enumValues = List.copyOf(enumValues);
		this.componentType = componentType;
	}

	@Override
	public CustomRegistry<ByteBuf, DataType<?>> getRegistry() {
		return REGISTRY;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	@Override
	public String toString() {
		var key = optionalKey();
		return key.isEmpty() ? ("Unregistered DataType " + codec) : key;
	}

	public DataType<List<T>> listOf() {
		return new DataType<>(codec.listOf(), KLibStreamCodecs.listOf(streamCodec), List.of(), this);
	}

	public DataType<Set<T>> setOf() {
		return new DataType<>(KLibCodecs.setOf(codec), KLibStreamCodecs.setOf(streamCodec), List.of(), this);
	}

	public ArgumentType<?> argument(CommandBuildContext ctx) {
		var commandInfo = DataTypeCommandInfoRegistry.MAP.get(this);

		if (commandInfo != null) {
			return commandInfo.argumentType().create(Cast.to(commandInfo), ctx);
		}

		if (!enumValues.isEmpty()) {
			//noinspection rawtypes,unchecked
			return new EnumDataTypeArgument(ref());
		} else {
			var ops = ctx.createSerializationContext(NbtOps.INSTANCE);
			return new ParsedDataTypeArgument<>(ops, TagParser.create(ops), ref());
		}
	}

	@Override
	public T get(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
		var commandInfo = DataTypeCommandInfoRegistry.MAP.get(this);

		if (commandInfo != null && commandInfo.argumentGetter() != null) {
			return ((ArgumentGetter<T>) commandInfo.argumentGetter()).get(ctx, name);
		}

		return (T) ctx.getArgument(name, Object.class);
	}

	@Nullable
	public Number toNumber(T value) {
		return switch (value) {
			case Number n -> n;
			case Boolean b -> b ? 1 : 0;
			case Character c -> (int) c;
			case NumberDataType t -> t.toNumber(this);
			case CharSequence s -> s.length();
			case Collection<?> c -> c.size();
			case Enum<?> e -> e.ordinal();
			case Map<?, ?> m -> m.size();
			case Component c -> c.getString().length();
			case Position p -> Mth.length(p.x(), p.y(), p.z());
			case Vec3i v -> Mth.length(v.getX(), v.getY(), v.getZ());
			case Identifier r -> r.toString().length();
			case ResourceKey<?> k -> k.identifier().toString().length();
			case null, default -> null;
		};
	}
}
