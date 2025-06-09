package dev.latvian.mods.klib.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.util.UndashedUuid;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.util.StringRepresentable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public interface KLibCodecs {
	static <T> Codec<List<T>> listOrSelf(Codec<T> elementCodec) {
		return Codec.either(elementCodec, elementCodec.listOf()).xmap(either -> either.map(List::of, Function.identity()), list -> list.size() == 1 ? Either.left(list.getFirst()) : Either.right(list));
	}

	Function<?, String> DEFAULT_NAME_GETTER = o -> o instanceof StringRepresentable s ? s.getSerializedName() : o instanceof Enum<?> e ? e.name().toLowerCase(Locale.ROOT) : o.toString().toLowerCase(Locale.ROOT);

	Codec<Unit> UNIT = Codec.unit(Unit.INSTANCE);
	Codec<UUID> UUID = Codec.STRING.xmap(UndashedUuid::fromStringLenient, UndashedUuid::toString);

	static <E> Codec<E> anyEnumCodec(E[] enumValues, Function<E, String> nameGetter) {
		var map = new HashMap<String, E>(enumValues.length);

		for (var value : enumValues) {
			map.put(nameGetter.apply(value), value);
		}

		return Codec.STRING.comapFlatMap(s -> {
			var e = map.get(s);

			if (e != null) {
				return DataResult.success(e);
			}

			return DataResult.error(() -> "Unknown enum value: " + s);
		}, nameGetter);
	}

	static <E> Codec<E> anyEnumCodec(E[] enumValues) {
		return anyEnumCodec(enumValues, (Function) DEFAULT_NAME_GETTER);
	}

	static <K, V> Codec<V> map(Supplier<Map<K, V>> mapGetter, Codec<K> keyCodec, Function<V, K> keyGetter) {
		return keyCodec.flatXmap(k -> {
			var map = mapGetter.get();

			if (map == null) {
				return DataResult.error(() -> "Map is null");
			} else if (map.isEmpty()) {
				return DataResult.error(() -> "Map is empty");
			} else {
				var value = map.get(k);
				return value == null ? DataResult.error(() -> "No value for key " + k) : DataResult.success(value);
			}
		}, v -> DataResult.success(keyGetter.apply(v)));
	}

	static <K, V> Codec<V> map(Map<K, V> map, Codec<K> keyCodec, Function<V, K> keyGetter) {
		Objects.requireNonNull(map, "Map is null");

		return keyCodec.flatXmap(k -> {
			if (map.isEmpty()) {
				return DataResult.error(() -> "Map is empty");
			} else {
				var value = map.get(k);
				return value == null ? DataResult.error(() -> "No value for key " + k) : DataResult.success(value);
			}
		}, v -> DataResult.success(keyGetter.apply(v)));
	}

	static <K, V> Codec<V> partialMap(Map<K, V> map, Codec<K> keyCodec, boolean identity) {
		Objects.requireNonNull(map, "Map is null");
		var reverseMap = identity ? new Reference2ObjectOpenHashMap<V, K>() : new Object2ObjectOpenHashMap<V, K>();

		return keyCodec.flatXmap(k -> {
			if (map.isEmpty()) {
				return DataResult.error(() -> "Map is empty");
			} else {
				var value = map.get(k);
				return value == null ? DataResult.error(() -> "No value for key " + k) : DataResult.success(value);
			}
		}, v -> {
			var key = reverseMap.get(v);
			return key != null ? DataResult.success(key) : DataResult.error(() -> "No key for value " + v);
		});
	}

	static <V> Codec<Optional<V>> optional(Codec<V> codec) {
		return Codec.either(UNIT, codec).xmap(either -> either.map(u -> Optional.empty(), Optional::of), opt -> opt.isPresent() ? Either.right(opt.get()) : Either.left(Unit.INSTANCE));
	}

	static <V> Codec<Set<V>> setOf(Codec<V> codec) {
		return Codec.list(codec).xmap(HashSet::new, ArrayList::new);
	}

	static <V> Codec<Set<V>> linkedSet(Codec<V> codec) {
		return Codec.list(codec).xmap(LinkedHashSet::new, ArrayList::new);
	}

	static <V> Codec<V> or(List<Codec<V>> codecs) {
		return new OrCodec<>(codecs);
	}

	static <V> Codec<V> or(Codec<V> first, Codec<V> second) {
		return new OrCodec<>(List.of(first, second));
	}
}
