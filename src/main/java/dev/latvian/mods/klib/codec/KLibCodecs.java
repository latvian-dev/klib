package dev.latvian.mods.klib.codec;

import com.mojang.brigadier.StringReader;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.util.UndashedUuid;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.commands.arguments.TimeArgument;
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
	Codec<UUID> UUID = Codec.STRING.comapFlatMap(s -> {
		try {
			return DataResult.success(UndashedUuid.fromStringLenient(s));
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid UUID syntax: " + s);
		}
	}, UndashedUuid::toString);

	TimeArgument TIME_ARGUMENT = TimeArgument.time();

	Codec<Integer> TICK_STRING = Codec.STRING.flatXmap(s -> {
		try {
			return DataResult.success(Objects.requireNonNull(TIME_ARGUMENT.parse(new StringReader(s))));
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid time format: " + s);
		}
	}, integer -> {
		int i = integer;

		if (i == 0) {
			return DataResult.success("0");
		} else if (i % 24000 == 0) {
			return DataResult.success(Integer.toUnsignedString(i / 24000) + "d");
		} else if (i % 20 == 0) {
			return DataResult.success(Integer.toUnsignedString(i / 20) + "s");
		} else {
			return DataResult.success(Integer.toUnsignedString(i));
		}
	});

	Codec<Integer> TICKS = Codec.either(Codec.INT, TICK_STRING).xmap(e -> e.map(Function.identity(), Function.identity()), Either::left);

	Codec<Integer> INT8 = Codec.intRange(Byte.MIN_VALUE, Byte.MAX_VALUE);
	Codec<Integer> UINT8 = Codec.intRange(0, -Byte.MIN_VALUE + Byte.MAX_VALUE);
	Codec<Integer> INT16 = Codec.intRange(Short.MIN_VALUE, Short.MAX_VALUE);
	Codec<Integer> UINT16 = Codec.intRange(0, -Short.MIN_VALUE + Short.MAX_VALUE);

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

		for (var entry : map.entrySet()) {
			var key = entry.getKey();
			var value = entry.getValue();
			reverseMap.put(value, key);
		}

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

	static <V> Codec<V> or(List<Codec<? extends V>> codecs) {
		return new OrCodec<>((List) codecs);
	}

	static <V> Codec<V> or(Codec<? extends V> first, Codec<? extends V> second) {
		return new OrCodec<>((List) List.of(first, second));
	}
}
