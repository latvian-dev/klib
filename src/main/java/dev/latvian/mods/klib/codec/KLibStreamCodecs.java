package dev.latvian.mods.klib.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.util.Empty;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public interface KLibStreamCodecs {
	StreamCodec<ByteBuf, Unit> UNIT = StreamCodec.unit(Unit.INSTANCE);

	StreamCodec<ByteBuf, Float> FLOAT_OR_ZERO = ByteBufCodecs.FLOAT.optional(0F);
	StreamCodec<ByteBuf, Float> FLOAT_OR_ONE = ByteBufCodecs.FLOAT.optional(1F);

	StreamCodec<ByteBuf, Double> DOUBLE_OR_ZERO = ByteBufCodecs.DOUBLE.optional(0D);
	StreamCodec<ByteBuf, Double> DOUBLE_OR_ONE = ByteBufCodecs.DOUBLE.optional(1D);

	StreamCodec<ByteBuf, Vec3> VEC3 = new StreamCodec<>() {
		@Override
		public Vec3 decode(ByteBuf buf) {
			return KMath.vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
		}

		@Override
		public void encode(ByteBuf buf, Vec3 value) {
			buf.writeDouble(value.x);
			buf.writeDouble(value.y);
			buf.writeDouble(value.z);
		}
	};

	StreamCodec<ByteBuf, Vec3> VEC3S = new StreamCodec<>() {
		@Override
		public Vec3 decode(ByteBuf buf) {
			if (buf.readBoolean()) {
				return KMath.vec3(buf.readDouble());
			} else {
				return KMath.vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
			}
		}

		@Override
		public void encode(ByteBuf buf, Vec3 value) {
			if (value.x == value.y && value.x == value.z) {
				buf.writeBoolean(true);
				buf.writeDouble(value.x);
			} else {
				buf.writeBoolean(false);
				buf.writeDouble(value.x);
				buf.writeDouble(value.y);
				buf.writeDouble(value.z);
			}
		}
	};

	StreamCodec<ByteBuf, Double> DOUBLE_AS_FLOAT = new StreamCodec<>() {
		@Override
		public Double decode(ByteBuf buf) {
			return (double) buf.readFloat();
		}

		@Override
		public void encode(ByteBuf buf, Double value) {
			buf.writeFloat(value.floatValue());
		}
	};

	StreamCodec<ByteBuf, AABB> AABB = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, b -> b.minX,
		ByteBufCodecs.DOUBLE, b -> b.minY,
		ByteBufCodecs.DOUBLE, b -> b.minZ,
		ByteBufCodecs.DOUBLE, b -> b.maxX,
		ByteBufCodecs.DOUBLE, b -> b.maxY,
		ByteBufCodecs.DOUBLE, b -> b.maxZ,
		AABB::new
	);

	StreamCodec<ByteBuf, SectionPos> SECTION_POS = ByteBufCodecs.LONG.map(SectionPos::of, SectionPos::asLong);

	StreamCodec<ByteBuf, UUID> UUID = new StreamCodec<>() {
		@Override
		public UUID decode(ByteBuf buf) {
			return new UUID(buf.readLong(), buf.readLong());
		}

		@Override
		public void encode(ByteBuf buf, UUID value) {
			buf.writeLong(value.getMostSignificantBits());
			buf.writeLong(value.getLeastSignificantBits());
		}
	};

	StreamCodec<RegistryFriendlyByteBuf, String> REGISTRY_STRING = new StreamCodec<>() {
		@Override
		public String decode(RegistryFriendlyByteBuf buf) {
			return buf.readUtf();
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, String value) {
			buf.writeUtf(value);
		}
	};

	StreamCodec<ByteBuf, CompoundTag> COMPOUND_TAG = new StreamCodec<>() {
		@Override
		public CompoundTag decode(ByteBuf buf) {
			var size = VarInt.read(buf);

			if (size == 0) {
				return Empty.COMPOUND_TAG;
			}

			var tag = new CompoundTag();

			for (int i = 0; i < size; i++) {
				tag.put(ByteBufCodecs.STRING_UTF8.decode(buf), ByteBufCodecs.TAG.decode(buf));
			}

			return tag;
		}

		@Override
		public void encode(ByteBuf buf, CompoundTag value) {
			VarInt.write(buf, value.size());

			if (!value.isEmpty()) {
				for (var key : value.keySet()) {
					ByteBufCodecs.STRING_UTF8.encode(buf, key);
					ByteBufCodecs.TAG.encode(buf, value.get(key));
				}
			}
		}
	};

	StreamCodec<ByteBuf, BlockState> BLOCK_STATE = ByteBufCodecs.VAR_INT.map(Block::stateById, Block::getId);
	StreamCodec<ByteBuf, FluidState> FLUID_STATE = ByteBufCodecs.VAR_INT.map(Fluid.FLUID_STATE_REGISTRY::byId, Fluid.FLUID_STATE_REGISTRY::getId);

	StreamCodec<ByteBuf, ResourceKey<Level>> DIMENSION = resourceKey(Registries.DIMENSION);

	static <T> StreamCodec<ByteBuf, ResourceKey<T>> resourceKey(ResourceKey<? extends Registry<T>> registry) {
		return ResourceLocation.STREAM_CODEC.map(id -> ResourceKey.create(registry, id), ResourceKey::location);
	}

	static <T> StreamCodec<ByteBuf, TagKey<T>> tagKey(ResourceKey<? extends Registry<T>> registry) {
		return ResourceLocation.STREAM_CODEC.map(id -> TagKey.create(registry, id), TagKey::location);
	}

	static <B extends ByteBuf, K, V> StreamCodec<B, V> map(Supplier<Map<K, V>> mapGetter, StreamCodec<B, K> keyCodec, Function<V, K> keyGetter) {
		return keyCodec.map(key -> mapGetter.get().get(key), keyGetter);
	}

	static <B extends ByteBuf, K, V> StreamCodec<B, V> map(Map<K, V> map, StreamCodec<B, K> keyCodec, Function<V, K> keyGetter) {
		Objects.requireNonNull(map, "Map is null");
		return keyCodec.map(map::get, keyGetter);
	}

	static <E extends Enum<E>> StreamCodec<ByteBuf, E> enumValue(Class<E> enumClass) {
		return enumValue(enumClass.getEnumConstants());
	}

	static <E extends Enum<E>> StreamCodec<ByteBuf, E> enumValue(E[] values) {
		return ByteBufCodecs.idMapper(i -> values[i], Enum::ordinal);
	}

	static <T> StreamCodec<ByteBuf, T> registry(Registry<T> registry) {
		return ByteBufCodecs.VAR_INT.map(registry::byIdOrThrow, registry::getId);
	}

	static <B extends ByteBuf, L, R> StreamCodec<B, Pair<L, R>> pair(StreamCodec<? super B, L> left, StreamCodec<? super B, R> right) {
		return StreamCodec.composite(left, Pair::getFirst, right, Pair::getSecond, Pair::of);
	}
}
