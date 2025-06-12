package dev.latvian.mods.klib.data;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.util.Cast;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.server.command.EnumArgument;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class DataType<T> {
	public static synchronized <T> void register(
		ResourceLocation id,
		DataType<T> type,
		@Nullable ArgumentTypeProvider<T> argumentType,
		@Nullable ArgumentGetter<T> argumentGetter
	) {
		var reg = new RegisteredDataType<>(id, type, argumentType, argumentGetter);
		RegisteredDataType.BY_ID.put(id, reg);
		RegisteredDataType.BY_TYPE.put(type, reg);
	}

	public static <T> void register(
		ResourceLocation id,
		DataType<T> type,
		@Nullable ArgumentTypeProvider.NS<T> argumentType,
		@Nullable ArgumentGetter<T> argumentGetter
	) {
		register(id, type, (ArgumentTypeProvider<T>) argumentType, argumentGetter);
	}

	public static <T> void register(
		ResourceLocation id,
		DataType<T> type,
		@Nullable ArgumentTypeProvider.NSNCTX<T> argumentType,
		@Nullable ArgumentGetter<T> argumentGetter
	) {
		register(id, type, (ArgumentTypeProvider<T>) argumentType, argumentGetter);
	}

	@SuppressWarnings("rawtypes")
	private static <T> EnumArgument enumArgument(RegisteredDataType<T> type, CommandBuildContext ctx) {
		return EnumArgument.enumArgument(Cast.to(type.type().typeClass));
	}

	@SuppressWarnings("unchecked")
	private static <T> T getEnumArgument(CommandContext<CommandSourceStack> ctx, String name) {
		return (T) ctx.getArgument(name, Enum.class);
	}

	public static <T> void register(ResourceLocation id, DataType<T> type) {
		if (type.typeClass.isEnum()) {
			register(id, type, DataType::enumArgument, DataType::getEnumArgument);
		} else {
			register(id, type, (ArgumentTypeProvider<T>) null, null);
		}
	}

	public static <T> DataType<T> of(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Class<T> typeClass) {
		return new DataType<>(codec, streamCodec, typeClass);
	}

	public static <E extends Enum<E>> DataType<E> of(E[] values, Function<E, String> nameGetter) {
		return of(
			KLibCodecs.anyEnumCodec(values, nameGetter),
			KLibStreamCodecs.enumValue(values),
			Cast.to(values.getClass().getComponentType())
		);
	}

	public static <E extends Enum<E>> DataType<E> of(E[] values) {
		return of(values, (Function<E, String>) KLibCodecs.DEFAULT_NAME_GETTER);
	}

	public static <T> DataType<ResourceKey<T>> of(ResourceKey<? extends Registry<T>> registry) {
		return of(
			ResourceKey.codec(registry),
			KLibStreamCodecs.resourceKey(registry),
			Cast.to(ResourceKey.class)
		);
	}

	public static <C, T extends C> DataType<C> unit(T value, Class<C> typeClass) {
		return of(Codec.unit(value), StreamCodec.unit(value), typeClass);
	}

	public static <L, R> DataType<Either<L, R>> either(DataType<L> left, DataType<R> right) {
		return of(
			Codec.either(left.codec(), right.codec()),
			ByteBufCodecs.either(left.streamCodec(), right.streamCodec()),
			Cast.to(Either.class)
		);
	}

	public static <T, L, R> DataType<T> either(DataType<L> left, DataType<R> right, Function<Either<L, R>, T> to, Function<T, Either<L, R>> from, Class<T> typeClass) {
		return of(
			Codec.either(left.codec(), right.codec()).xmap(to, from),
			ByteBufCodecs.either(left.streamCodec(), right.streamCodec()).map(to, from),
			typeClass
		);
	}

	private final Codec<T> codec;
	private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;
	private final Class<T> typeClass;
	private DataType<List<T>> listType;
	private DataType<Set<T>> setType;

	private DataType(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Class<T> typeClass) {
		this.codec = codec;
		this.streamCodec = streamCodec;
		this.typeClass = typeClass;
	}

	public Codec<T> codec() {
		return codec;
	}

	public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
		return streamCodec;
	}

	public Class<T> typeClass() {
		return typeClass;
	}

	public DataType<List<T>> listOf() {
		if (listType == null) {
			listType = of(codec.listOf(), streamCodec.listOf(), Cast.to(List.class));
		}

		return listType;
	}

	public DataType<Set<T>> setOf() {
		if (setType == null) {
			setType = of(KLibCodecs.setOf(codec), streamCodec.setOf(), Cast.to(Set.class));
		}

		return setType;
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
			case ResourceLocation r -> r.toString().length();
			case ResourceKey<?> k -> k.location().toString().length();
			case null, default -> null;
		};
	}

	public <R> DataType<R> map(Function<T, R> mapper, Function<R, T> reverseMapper, Class<R> typeClass) {
		return of(codec.xmap(mapper, reverseMapper), streamCodec.map(mapper, reverseMapper), typeClass);
	}
}
