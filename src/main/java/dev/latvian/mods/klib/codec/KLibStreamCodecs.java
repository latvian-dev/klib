package dev.latvian.mods.klib.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

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
