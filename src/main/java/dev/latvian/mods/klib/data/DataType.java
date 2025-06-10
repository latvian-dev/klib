package dev.latvian.mods.klib.data;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.util.Cast;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.server.command.EnumArgument;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public final class DataType<T> {
	public static <T> void register(
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
		var reg = new RegisteredDataType<>(id, type, argumentType, argumentGetter);
		RegisteredDataType.BY_ID.put(id, reg);
		RegisteredDataType.BY_TYPE.put(type, reg);
	}

	public static <T> void register(
		ResourceLocation id,
		DataType<T> type,
		@Nullable ArgumentTypeProvider.NSNCTX<T> argumentType,
		@Nullable ArgumentGetter<T> argumentGetter
	) {
		var reg = new RegisteredDataType<>(id, type, argumentType, argumentGetter);
		RegisteredDataType.BY_ID.put(id, reg);
		RegisteredDataType.BY_TYPE.put(type, reg);
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

	private static final Function<Collection<?>, Number> COLLECTION_SIZE_CONVERTER = Collection::size;

	public static <T> DataType<T> of(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Class<T> typeClass, @Nullable Function<T, Number> numberConverter) {
		return new DataType<>(codec, streamCodec, typeClass, numberConverter);
	}

	public static <T> DataType<T> of(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Class<T> typeClass) {
		return of(codec, streamCodec, typeClass, null);
	}

	public static <E extends Enum<E>> DataType<E> of(E[] values, Function<E, String> nameGetter) {
		return of(
			KLibCodecs.anyEnumCodec(values, nameGetter),
			KLibStreamCodecs.enumValue(values),
			Cast.to(values.getClass().getComponentType())
		).withNumberConverter(Enum::ordinal);
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

	private final Codec<T> codec;
	private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;
	private final Class<T> typeClass;
	private final @Nullable Function<T, Number> numberConverter;
	private DataType<List<T>> listType;
	private DataType<Set<T>> setType;

	private DataType(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Class<T> typeClass, @Nullable Function<T, Number> numberConverter) {
		this.codec = codec;
		this.streamCodec = streamCodec;
		this.typeClass = typeClass;
		this.numberConverter = numberConverter;
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

	public DataType<T> withNumberConverter(Function<T, Number> numberConverter) {
		return of(codec, streamCodec, typeClass, numberConverter);
	}

	public DataType<List<T>> listOf() {
		if (listType == null) {
			listType = of(codec.listOf(), streamCodec.listOf(), Cast.to(List.class), (Function) COLLECTION_SIZE_CONVERTER);
		}

		return listType;
	}

	public DataType<Set<T>> setOf() {
		if (setType == null) {
			setType = of(KLibCodecs.setOf(codec), streamCodec.setOf(), Cast.to(Set.class), (Function) COLLECTION_SIZE_CONVERTER);
		}

		return setType;
	}

	@Nullable
	public Number toNumber(T value) {
		return numberConverter == null ? null : numberConverter.apply(value);
	}
}
