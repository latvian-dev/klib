package dev.latvian.mods.klib.data;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function10;
import com.mojang.datafixers.util.Function11;
import com.mojang.datafixers.util.Function12;
import com.mojang.datafixers.util.Function13;
import com.mojang.datafixers.util.Function14;
import com.mojang.datafixers.util.Function15;
import com.mojang.datafixers.util.Function16;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.util.Cast;
import dev.latvian.mods.klib.util.MethodHandleInvoker;
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

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
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

	public static <T> DataTypeBuilder<T> builder(Class<T> typeClass) {
		return new DataTypeBuilder<>(typeClass);
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

	public <C> DataType<C> buildRaw(Class<C> typeClass, List<DataTypeField<C, ?>> fields, Function<Object[], C> constructor) {
		var list = List.copyOf(fields);

		return DataType.of(
			new DataTypeBuilderCodec<>(list, constructor).codec(),
			new DataTypeBuilderStreamCodec<>(list, constructor),
			typeClass
		);
	}

	public <C> DataType<C> build(
		Class<C> typeClass,
		List<DataTypeField<C, ?>> fields
	) {
		var lookup = MethodHandles.publicLookup();
		List<Class<?>> types = Cast.to(fields.stream().map(DataTypeField::typeClass).toList());
		var methodType = MethodType.methodType(void.class, types);

		try {
			var constructor = lookup.findConstructor(typeClass, methodType);
			return buildRaw(typeClass, fields, new MethodHandleInvoker<>(constructor));
		} catch (Exception ex) {
			throw new RuntimeException("Couldn't construct DataType, constructor not found", ex);
		}
	}

	public <C, T1> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		Function<T1, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1), args -> constructor.apply((T1) args[0]));
	}

	public <C, T1, T2> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		BiFunction<T1, T2, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2), args -> constructor.apply((T1) args[0], (T2) args[1]));
	}

	public <C, T1, T2, T3> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		Function3<T1, T2, T3, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2]));
	}

	public <C, T1, T2, T3, T4> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		DataTypeField<C, T4> f4,
		Function4<T1, T2, T3, T4, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3, f4), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3]));
	}

	public <C, T1, T2, T3, T4, T5> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		DataTypeField<C, T4> f4,
		DataTypeField<C, T5> f5,
		Function5<T1, T2, T3, T4, T5, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3, f4, f5), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4]));
	}

	public <C, T1, T2, T3, T4, T5, T6> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		DataTypeField<C, T4> f4,
		DataTypeField<C, T5> f5,
		DataTypeField<C, T6> f6,
		Function6<T1, T2, T3, T4, T5, T6, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3, f4, f5, f6), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4], (T6) args[5]));
	}

	public <C, T1, T2, T3, T4, T5, T6, T7> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		DataTypeField<C, T4> f4,
		DataTypeField<C, T5> f5,
		DataTypeField<C, T6> f6,
		DataTypeField<C, T7> f7,
		Function7<T1, T2, T3, T4, T5, T6, T7, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3, f4, f5, f6, f7), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4], (T6) args[5], (T7) args[6]));
	}

	public <C, T1, T2, T3, T4, T5, T6, T7, T8> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		DataTypeField<C, T4> f4,
		DataTypeField<C, T5> f5,
		DataTypeField<C, T6> f6,
		DataTypeField<C, T7> f7,
		DataTypeField<C, T8> f8,
		Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3, f4, f5, f6, f7, f8), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4], (T6) args[5], (T7) args[6], (T8) args[7]));
	}

	public <C, T1, T2, T3, T4, T5, T6, T7, T8, T9> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		DataTypeField<C, T4> f4,
		DataTypeField<C, T5> f5,
		DataTypeField<C, T6> f6,
		DataTypeField<C, T7> f7,
		DataTypeField<C, T8> f8,
		DataTypeField<C, T9> f9,
		Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3, f4, f5, f6, f7, f8, f9), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4], (T6) args[5], (T7) args[6], (T8) args[7], (T9) args[8]));
	}

	public <C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		DataTypeField<C, T4> f4,
		DataTypeField<C, T5> f5,
		DataTypeField<C, T6> f6,
		DataTypeField<C, T7> f7,
		DataTypeField<C, T8> f8,
		DataTypeField<C, T9> f9,
		DataTypeField<C, T10> f10,
		Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4], (T6) args[5], (T7) args[6], (T8) args[7], (T9) args[8], (T10) args[9]));
	}

	public <C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		DataTypeField<C, T4> f4,
		DataTypeField<C, T5> f5,
		DataTypeField<C, T6> f6,
		DataTypeField<C, T7> f7,
		DataTypeField<C, T8> f8,
		DataTypeField<C, T9> f9,
		DataTypeField<C, T10> f10,
		DataTypeField<C, T11> f11,
		Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4], (T6) args[5], (T7) args[6], (T8) args[7], (T9) args[8], (T10) args[9], (T11) args[10]));
	}

	public <C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		DataTypeField<C, T4> f4,
		DataTypeField<C, T5> f5,
		DataTypeField<C, T6> f6,
		DataTypeField<C, T7> f7,
		DataTypeField<C, T8> f8,
		DataTypeField<C, T9> f9,
		DataTypeField<C, T10> f10,
		DataTypeField<C, T11> f11,
		DataTypeField<C, T12> f12,
		Function12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4], (T6) args[5], (T7) args[6], (T8) args[7], (T9) args[8], (T10) args[9], (T11) args[10], (T12) args[11]));
	}

	public <C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		DataTypeField<C, T4> f4,
		DataTypeField<C, T5> f5,
		DataTypeField<C, T6> f6,
		DataTypeField<C, T7> f7,
		DataTypeField<C, T8> f8,
		DataTypeField<C, T9> f9,
		DataTypeField<C, T10> f10,
		DataTypeField<C, T11> f11,
		DataTypeField<C, T12> f12,
		DataTypeField<C, T13> f13,
		Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4], (T6) args[5], (T7) args[6], (T8) args[7], (T9) args[8], (T10) args[9], (T11) args[10], (T12) args[11], (T13) args[12]));
	}

	public <C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		DataTypeField<C, T4> f4,
		DataTypeField<C, T5> f5,
		DataTypeField<C, T6> f6,
		DataTypeField<C, T7> f7,
		DataTypeField<C, T8> f8,
		DataTypeField<C, T9> f9,
		DataTypeField<C, T10> f10,
		DataTypeField<C, T11> f11,
		DataTypeField<C, T12> f12,
		DataTypeField<C, T13> f13,
		DataTypeField<C, T14> f14,
		Function14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4], (T6) args[5], (T7) args[6], (T8) args[7], (T9) args[8], (T10) args[9], (T11) args[10], (T12) args[11], (T13) args[12], (T14) args[13]));
	}

	public <C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		DataTypeField<C, T4> f4,
		DataTypeField<C, T5> f5,
		DataTypeField<C, T6> f6,
		DataTypeField<C, T7> f7,
		DataTypeField<C, T8> f8,
		DataTypeField<C, T9> f9,
		DataTypeField<C, T10> f10,
		DataTypeField<C, T11> f11,
		DataTypeField<C, T12> f12,
		DataTypeField<C, T13> f13,
		DataTypeField<C, T14> f14,
		DataTypeField<C, T15> f15,
		Function15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4], (T6) args[5], (T7) args[6], (T8) args[7], (T9) args[8], (T10) args[9], (T11) args[10], (T12) args[11], (T13) args[12], (T14) args[13], (T15) args[14]));
	}

	public <C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> DataType<C> build(
		Class<C> typeClass,
		DataTypeField<C, T1> f1,
		DataTypeField<C, T2> f2,
		DataTypeField<C, T3> f3,
		DataTypeField<C, T4> f4,
		DataTypeField<C, T5> f5,
		DataTypeField<C, T6> f6,
		DataTypeField<C, T7> f7,
		DataTypeField<C, T8> f8,
		DataTypeField<C, T9> f9,
		DataTypeField<C, T10> f10,
		DataTypeField<C, T11> f11,
		DataTypeField<C, T12> f12,
		DataTypeField<C, T13> f13,
		DataTypeField<C, T14> f14,
		DataTypeField<C, T15> f15,
		DataTypeField<C, T16> f16,
		Function16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, C> constructor
	) {
		return buildRaw(typeClass, List.of(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16), args -> constructor.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4], (T6) args[5], (T7) args[6], (T8) args[7], (T9) args[8], (T10) args[9], (T11) args[10], (T12) args[11], (T13) args[12], (T14) args[13], (T15) args[14], (T16) args[15]));
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

	public <C> DataTypeField<C, T> field(String name, Function<C, T> getter) {
		return new DataTypeField.RequiredField<>(this, name, getter);
	}

	public <C> DataTypeField<C, Optional<T>> optionalField(String name, Function<C, Optional<T>> getter) {
		return new DataTypeField.OptionalField<>(this, name, getter);
	}

	public <C> DataTypeField<C, T> optionalField(String name, Function<C, T> getter, T defaultValue) {
		return new DataTypeField.OptionalDefaultField<>(this, name, getter, Objects.requireNonNull(defaultValue));
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
