package dev.latvian.mods.klib.codec;

import com.mojang.brigadier.StringReader;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.util.UndashedUuid;
import dev.latvian.mods.klib.util.NameProvider;
import dev.latvian.mods.klib.util.StringUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.IdentifierException;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface KLibCodecs {
	static <T> Codec<List<T>> listOrSelf(Codec<T> elementCodec) {
		return Codec.either(elementCodec, elementCodec.listOf()).xmap(either -> either.map(List::of, Function.identity()), list -> list.size() == 1 ? Either.left(list.getFirst()) : Either.right(list));
	}

	Codec<Unit> UNIT = MapCodec.unitCodec(Unit.INSTANCE);
	Codec<String> INTERN_STRING = Codec.STRING.xmap(String::intern, Function.identity());

	Codec<String> INTERN_PATH = INTERN_STRING.validate(s -> {
		if (Identifier.isValidPath(s)) {
			return DataResult.success(s);
		} else {
			return DataResult.error(() -> "Non [a-z0-9/._-] character in path " + s);
		}
	});

	static String readInternPath(StringReader reader) {
		int start = reader.getCursor();

		while (reader.canRead() && Identifier.isAllowedInIdentifier(reader.peek())) {
			reader.skip();
		}

		return reader.getString().substring(start, reader.getCursor()).intern();
	}

	Codec<UUID> UUID = Codec.STRING.comapFlatMap(s -> {
		try {
			return DataResult.success(UndashedUuid.fromStringLenient(s));
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid UUID syntax: " + s);
		}
	}, UndashedUuid::toString);

	Codec<UUID> DASHED_UUID = Codec.STRING.comapFlatMap(s -> {
		try {
			return DataResult.success(UndashedUuid.fromStringLenient(s));
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid UUID syntax: " + s);
		}
	}, java.util.UUID::toString);

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

	Codec<byte[]> B64_BYTE_ARRAY = Codec.STRING.flatXmap(string -> {
		try {
			return DataResult.success(StringUtils.B64_DECODER.decode(string));
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid Base64 string: " + string);
		}
	}, array -> {
		try {
			return DataResult.success(StringUtils.B64_ENCODER.encodeToString(array));
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid Base64 array: " + Arrays.toString(array));
		}
	});

	Codec<Instant> ISO_INSTANT = Codec.STRING.flatXmap(string -> {
		try {
			return DataResult.success(Instant.parse(string));
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid date: " + string);
		}
	}, instant -> {
		try {
			return DataResult.success(instant.toString());
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid date: " + instant);
		}
	});

	Codec<Instant> UINT64_INSTANT = Codec.LONG.flatXmap(number -> {
		try {
			return DataResult.success(Instant.ofEpochMilli(number));
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid date: " + number);
		}
	}, instant -> {
		try {
			return DataResult.success(instant.toEpochMilli());
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid date: " + instant);
		}
	});

	Codec<Instant> INSTANT = KLibCodecs.or(ISO_INSTANT, UINT64_INSTANT);

	Codec<URI> URI = Codec.STRING.flatXmap(string -> {
		try {
			return DataResult.success(new java.net.URI(string));
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid URI: " + string);
		}
	}, uri -> {
		try {
			return DataResult.success(uri.toString());
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid URI: " + uri);
		}
	});

	static <E> Codec<E> anyEnum(E[] enumValues, @Nullable NameProvider<E> nameProvider) {
		var map0 = new HashMap<String, E>(enumValues.length);
		var provider = NameProvider.resolve(nameProvider);

		for (var value : enumValues) {
			map0.put(provider.provideName(value), value);
		}

		var map = Map.copyOf(map0);

		return Codec.STRING.comapFlatMap(s -> {
			var e = map.get(s);

			if (e != null) {
				return DataResult.success(e);
			}

			return DataResult.error(() -> "Unknown enum value: " + s);
		}, provider.toFunction());
	}

	static <E> Codec<E> anyEnum(E[] enumValues) {
		return anyEnum(enumValues, null);
	}

	static <K, V> Codec<V> map(Supplier<Map<K, V>> mapGetter, Codec<K> keyCodec, Function<V, K> keyGetter) {
		return keyCodec.flatXmap(k -> {
			var map = mapGetter.get();

			if (map == null) {
				return KLibCodecErrors.mapIsNull();
			} else if (map.isEmpty()) {
				return KLibCodecErrors.mapIsEmpty();
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
				return KLibCodecErrors.mapIsEmpty();
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
				return KLibCodecErrors.mapIsEmpty();
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

	static Codec<Identifier> commonIdentifier(String namespace) {
		if (namespace.isEmpty()) {
			return Identifier.CODEC;
		}

		var commonIdentifier = Identifier.fromNamespaceAndPath(namespace, "x");

		return Codec.STRING.comapFlatMap(input -> {
			try {
				if (input.indexOf(':') == -1) {
					return DataResult.success(commonIdentifier.withPath(input));
				} else {
					return DataResult.success(Identifier.parse(input));
				}
			} catch (IdentifierException var2) {
				return DataResult.error(() -> "Not a valid resource location: " + input + " " + var2.getMessage());
			}
		}, id -> id.getNamespace().equals(commonIdentifier.getNamespace()) ? id.getPath() : id.toString());
	}

	static <T> Codec<ResourceKey<T>> commonResourceKey(ResourceKey<? extends Registry<T>> root, String namespace) {
		if (namespace.isEmpty()) {
			return ResourceKey.codec(root);
		}

		var commonIdentifier = Identifier.fromNamespaceAndPath(namespace, "x");

		return Codec.STRING.comapFlatMap(input -> {
			try {
				if (input.indexOf(':') == -1) {
					return DataResult.success(ResourceKey.create(root, commonIdentifier.withPath(input)));
				} else {
					return DataResult.success(ResourceKey.create(root, Identifier.parse(input)));
				}
			} catch (IdentifierException var2) {
				return DataResult.error(() -> "Not a valid resource location: " + input + " " + var2.getMessage());
			}
		}, key -> {
			var id = key.identifier();
			return id.getNamespace().equals(commonIdentifier.getNamespace()) ? id.getPath() : id.toString();
		});
	}

	static <V, T> Codec<T> unit(V unitValue, T resultValue, Predicate<T> isUnit) {
		return MapCodec.unitCodec(unitValue).flatXmap(value -> {
			if (unitValue.equals(value)) {
				return DataResult.success(resultValue);
			} else {
				return DataResult.error(() -> "Not unit");
			}
		}, value -> {
			if (isUnit.test(value)) {
				return DataResult.success(unitValue);
			} else {
				return DataResult.error(() -> "Not empty value");
			}
		});
	}

	static <K, V> Codec<Map.Entry<K, V>> mapEntry(Codec<K> keyCodec, Codec<V> valueCodec) {
		return Codec.unboundedMap(keyCodec, valueCodec).comapFlatMap(map -> {
			if (map.size() == 1) {
				return DataResult.success(map.entrySet().iterator().next());
			} else {
				return DataResult.error(() -> "Map must have exactly one entry");
			}
		}, entry -> Map.of(entry.getKey(), entry.getValue()));
	}
}
