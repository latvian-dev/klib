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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public interface KLibStreamCodecs {
	StreamCodec<ByteBuf, Unit> UNIT = StreamCodec.unit(Unit.INSTANCE);

	static <B extends ByteBuf, V> StreamCodec<B, V> optional(StreamCodec<B, V> parent, @Nullable V defaultValue) {
		return new OptionalDefaultStreamCodec<>(parent, defaultValue);
	}

	static <B extends ByteBuf, V> StreamCodec<B, V> nullable(StreamCodec<B, V> parent) {
		return optional(parent, null);
	}

	static <B extends ByteBuf, V> StreamCodec<B, List<V>> listOf(StreamCodec<? super B, V> parent) {
		return new ListStreamCodec<>(parent);
	}

	static <B extends ByteBuf, V> StreamCodec<B, Set<V>> setOf(StreamCodec<? super B, V> parent) {
		return new SetStreamCodec<>(parent);
	}

	static <B extends ByteBuf, V> StreamCodec<B, Set<V>> linkedSetOf(StreamCodec<? super B, V> parent) {
		return new LinkedSetStreamCodec<>(parent);
	}

	static <B extends ByteBuf, K, V> StreamCodec<B, Map<K, V>> unboundedMap(StreamCodec<? super B, K> keyCodec, StreamCodec<? super B, V> valueCodec, boolean ordered, boolean identity) {
		return new UnboundMapStreamCodec<>(keyCodec, valueCodec, ordered, identity);
	}

	static <B extends ByteBuf, K, V> StreamCodec<B, Map<K, V>> unboundedMap(StreamCodec<? super B, K> keyCodec, StreamCodec<? super B, V> valueCodec) {
		return unboundedMap(keyCodec, valueCodec, false, false);
	}

	StreamCodec<ByteBuf, Float> FLOAT_OR_ZERO = optional(ByteBufCodecs.FLOAT, 0F);
	StreamCodec<ByteBuf, Float> FLOAT_OR_ONE = optional(ByteBufCodecs.FLOAT, 1F);

	StreamCodec<ByteBuf, Double> DOUBLE_OR_ZERO = optional(ByteBufCodecs.DOUBLE, 0D);
	StreamCodec<ByteBuf, Double> DOUBLE_OR_ONE = optional(ByteBufCodecs.DOUBLE, 1D);

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
