package dev.latvian.mods.klib.data;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.util.Cast;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public record DataType<T>(
	Codec<T> codec,
	StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec,
	Class<T> typeClass,
	@Nullable Function<T, Number> numberConverter
) {
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

	public static final DataType<Boolean> BOOL = of(Codec.BOOL, ByteBufCodecs.BOOL, Boolean.class, v -> v ? 1 : 0);
	public static final DataType<Integer> INT = of(Codec.INT, ByteBufCodecs.INT, Integer.class, v -> v);
	public static final DataType<Integer> VAR_INT = of(Codec.INT, ByteBufCodecs.VAR_INT, Integer.class, v -> v);
	public static final DataType<Long> LONG = of(Codec.LONG, ByteBufCodecs.LONG, Long.class, v -> v);
	public static final DataType<Long> VAR_LONG = of(Codec.LONG, ByteBufCodecs.VAR_LONG, Long.class, v -> v);
	public static final DataType<Float> FLOAT = of(Codec.FLOAT, ByteBufCodecs.FLOAT, Float.class, v -> v);
	public static final DataType<Double> DOUBLE = of(Codec.DOUBLE, ByteBufCodecs.DOUBLE, Double.class, v -> v);
	public static final DataType<String> STRING = of(Codec.STRING, ByteBufCodecs.STRING_UTF8, String.class, String::length);
	public static final DataType<UUID> UUID = of(KLibCodecs.UUID, KLibStreamCodecs.UUID, UUID.class);

	public static final DataType<ResourceLocation> ID = of(dev.latvian.mods.klib.util.ID.CODEC, dev.latvian.mods.klib.util.ID.STREAM_CODEC, ResourceLocation.class, v -> v.toString().length());
	public static final DataType<Component> TEXT_COMPONENT = of(ComponentSerialization.CODEC, ComponentSerialization.STREAM_CODEC, Component.class, v -> v.getString().length());
	public static final DataType<Mirror> MIRROR = of(Mirror.values());
	public static final DataType<Rotation> BLOCK_ROTATION = of(Rotation.values());
	public static final DataType<LiquidSettings> LIQUID_SETTINGS = of(LiquidSettings.values());
	public static final DataType<InteractionHand> HAND = of(InteractionHand.values());
	public static final DataType<SoundSource> SOUND_SOURCE = of(SoundSource.values());
	public static final DataType<ItemStack> ITEM_STACK = of(ItemStack.OPTIONAL_CODEC, ItemStack.OPTIONAL_STREAM_CODEC, ItemStack.class);
	public static final DataType<ParticleOptions> PARTICLE_OPTIONS = of(ParticleTypes.CODEC, ParticleTypes.STREAM_CODEC, ParticleOptions.class);
	public static final DataType<BlockState> BLOCK_STATE = of(KLibCodecs.BLOCK_STATE, KLibStreamCodecs.BLOCK_STATE, BlockState.class);
	public static final DataType<FluidState> FLUID_STATE = of(KLibCodecs.FLUID_STATE, KLibStreamCodecs.FLUID_STATE, FluidState.class);
	public static final DataType<Vec3> VEC3 = of(Vec3.CODEC, Vec3.STREAM_CODEC, Vec3.class, Vec3::length);
	public static final DataType<BlockPos> BLOCK_POS = of(BlockPos.CODEC, BlockPos.STREAM_CODEC, BlockPos.class, v -> Vec3.atLowerCornerOf(v).length());

	public DataType<T> withNumberConverter(Function<T, Number> numberConverter) {
		return of(codec, streamCodec, typeClass, numberConverter);
	}

	public DataType<List<T>> listOf() {
		return of(codec.listOf(), streamCodec.listOf(), Cast.to(List.class), (Function) COLLECTION_SIZE_CONVERTER);
	}

	public DataType<Set<T>> setOf() {
		return of(KLibCodecs.setOf(codec), streamCodec.setOf(), Cast.to(Set.class), (Function) COLLECTION_SIZE_CONVERTER);
	}

	@Nullable
	public Number toNumber(T value) {
		return numberConverter == null ? null : numberConverter.apply(value);
	}
}
