package dev.latvian.mods.klib.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import dev.latvian.mods.klib.util.Cast;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.VarLong;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public interface KLibStreamCodecs {
	StreamCodec<ByteBuf, Unit> UNIT = StreamCodec.unit(Unit.INSTANCE);

	static <V> StreamCodec<ByteBuf, V> toBasic(StreamCodec<? super RegistryFriendlyByteBuf, V> parent) {
		return new StreamCodec<>() {
			@Override
			public V decode(ByteBuf buf) {
				return parent.decode(Cast.to(buf));
			}

			@Override
			public void encode(ByteBuf buf, V value) {
				parent.encode(Cast.to(buf), value);
			}
		};
	}

	static <V> StreamCodec<FriendlyByteBuf, V> toFriendly(StreamCodec<? super RegistryFriendlyByteBuf, V> parent) {
		return new StreamCodec<>() {
			@Override
			public V decode(FriendlyByteBuf buf) {
				return parent.decode(Cast.to(buf));
			}

			@Override
			public void encode(FriendlyByteBuf buf, V value) {
				parent.encode(Cast.to(buf), value);
			}
		};
	}

	static <V> StreamCodec<RegistryFriendlyByteBuf, V> toRegistry(StreamCodec<? super RegistryFriendlyByteBuf, V> parent) {
		return new StreamCodec<>() {
			@Override
			public V decode(RegistryFriendlyByteBuf buf) {
				return parent.decode(buf);
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buf, V value) {
				parent.encode(buf, value);
			}
		};
	}

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

	StreamCodec<ByteBuf, Double> DOUBLE32 = new StreamCodec<>() {
		@Override
		public Double decode(ByteBuf buf) {
			return (double) buf.readFloat();
		}

		@Override
		public void encode(ByteBuf buf, Double value) {
			buf.writeFloat(value.floatValue());
		}
	};

	StreamCodec<ByteBuf, Integer> INT8 = new StreamCodec<>() {
		@Override
		public Integer decode(ByteBuf buf) {
			return (int) buf.readByte();
		}

		@Override
		public void encode(ByteBuf buf, Integer value) {
			buf.writeByte(value);
		}
	};

	StreamCodec<ByteBuf, Integer> UINT8 = new StreamCodec<>() {
		@Override
		public Integer decode(ByteBuf buf) {
			return ((int) buf.readByte()) & 0xFF;
		}

		@Override
		public void encode(ByteBuf buf, Integer value) {
			buf.writeByte(value);
		}
	};

	StreamCodec<ByteBuf, Integer> INT16 = new StreamCodec<>() {
		@Override
		public Integer decode(ByteBuf buf) {
			return (int) buf.readShort();
		}

		@Override
		public void encode(ByteBuf buf, Integer value) {
			buf.writeShort(value);
		}
	};

	StreamCodec<ByteBuf, Integer> UINT16 = new StreamCodec<>() {
		@Override
		public Integer decode(ByteBuf buf) {
			return ((int) buf.readShort()) & 0xFFFF;
		}

		@Override
		public void encode(ByteBuf buf, Integer value) {
			buf.writeShort(value);
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

	StreamCodec<ByteBuf, Instant> INSTANT = new StreamCodec<>() {
		@Override
		public Instant decode(ByteBuf buf) {
			var second = VarLong.read(buf);
			var nano = VarInt.read(buf);
			return Instant.ofEpochSecond(second, nano);
		}

		@Override
		public void encode(ByteBuf buf, Instant value) {
			VarLong.write(buf, value.getEpochSecond());
			VarInt.write(buf, value.getNano());
		}
	};

	static <T> StreamCodec<ByteBuf, ResourceKey<T>> resourceKey(ResourceKey<? extends Registry<T>> registry) {
		return Identifier.STREAM_CODEC.map(id -> ResourceKey.create(registry, id), ResourceKey::identifier);
	}

	static <T> StreamCodec<ByteBuf, TagKey<T>> tagKey(ResourceKey<? extends Registry<T>> registry) {
		return Identifier.STREAM_CODEC.map(id -> TagKey.create(registry, id), TagKey::location);
	}

	static <B extends ByteBuf, K, V> StreamCodec<B, V> map(Supplier<Map<K, V>> mapGetter, StreamCodec<B, K> keyCodec, Function<V, K> keyGetter) {
		return keyCodec.map(key -> mapGetter.get().get(key), keyGetter);
	}

	static <B extends ByteBuf, K, V> StreamCodec<B, V> map(Map<K, V> map, StreamCodec<B, K> keyCodec, Function<V, K> keyGetter) {
		Objects.requireNonNull(map, "Map is null");
		return keyCodec.map(map::get, keyGetter);
	}

	static <E> StreamCodec<ByteBuf, E> anyEnum(E[] values, ToIntFunction<E> ordinalFunction) {
		return ByteBufCodecs.idMapper(i -> values[i], ordinalFunction);
	}

	static <E extends Enum<E>> StreamCodec<ByteBuf, E> enumClass(Class<E> enumClass) {
		return anyEnum(enumClass.getEnumConstants(), Enum::ordinal);
	}

	static <E> StreamCodec<ByteBuf, E> anyEnum(E[] values) {
		if (values.length == 0) {
			throw new IllegalArgumentException("Values is empty");
		}

		if (values.getClass().getComponentType().isEnum() && Arrays.equals(values, values.getClass().getComponentType().getEnumConstants())) {
			return Cast.to(enumClass(Cast.to(values.getClass().getComponentType())));
		}

		var map = new Reference2IntArrayMap<E>(values.length);

		for (int i = 0; i < values.length; i++) {
			map.put(values[i], i);
		}

		return anyEnum(values, map);
	}

	static <T> StreamCodec<ByteBuf, T> registry(Registry<T> registry) {
		return ByteBufCodecs.VAR_INT.map(registry::byIdOrThrow, registry::getId);
	}

	static <B extends ByteBuf, L, R> StreamCodec<B, Pair<L, R>> pair(StreamCodec<? super B, L> left, StreamCodec<? super B, R> right) {
		return CompositeStreamCodec.of(left, Pair::getFirst, right, Pair::getSecond, Pair::of);
	}

	static StreamCodec<ByteBuf, Identifier> commonIdentifier(String namespace) {
		if (namespace.isEmpty()) {
			return Identifier.STREAM_CODEC;
		}

		return new StreamCodec<>() {
			private final Identifier commonIdentifier = Identifier.fromNamespaceAndPath(namespace, "x");

			@Override
			public Identifier decode(ByteBuf buf) {
				var string = ByteBufCodecs.STRING_UTF8.decode(buf);
				return string.indexOf(':') == -1 ? commonIdentifier.withPath(string) : Identifier.parse(string);
			}

			@Override
			public void encode(ByteBuf buf, Identifier id) {
				ByteBufCodecs.STRING_UTF8.encode(buf, id.getNamespace().equals(commonIdentifier.getNamespace()) ? id.getPath() : id.toString());
			}
		};
	}

	static <T> StreamCodec<ByteBuf, ResourceKey<T>> commonResourceKey(ResourceKey<? extends Registry<T>> root, String namespace) {
		if (namespace.isEmpty()) {
			return ResourceKey.streamCodec(root);
		}

		return new StreamCodec<>() {
			private final Identifier commonIdentifier = Identifier.fromNamespaceAndPath(namespace, "x");

			@Override
			public ResourceKey<T> decode(ByteBuf buf) {
				var string = ByteBufCodecs.STRING_UTF8.decode(buf);
				return ResourceKey.create(root, string.indexOf(':') == -1 ? commonIdentifier.withPath(string) : Identifier.parse(string));
			}

			@Override
			public void encode(ByteBuf buf, ResourceKey<T> value) {
				var id = value.identifier();
				ByteBufCodecs.STRING_UTF8.encode(buf, id.getNamespace().equals(commonIdentifier.getNamespace()) ? id.getPath() : id.toString());
			}
		};
	}
}
