package dev.latvian.mods.klib.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public interface CollectionCodecs {
	Codec<IntList> INT_LIST = Codec.INT.listOf().xmap(IntArrayList::new, Function.identity());
	Codec<IntList> INT_LIST_OR_SELF = Codec.either(Codec.INT, INT_LIST).xmap(either -> either.map(IntArrayList::of, Function.identity()), list -> list.size() == 1 ? Either.left(list.getFirst()) : Either.right(list));
	Codec<ShortList> SHORT_LIST = Codec.SHORT.listOf().xmap(ShortArrayList::new, Function.identity());
	Codec<IntSet> INT_SET = KLibCodecs.setOf(Codec.INT).xmap(IntOpenHashSet::new, Function.identity());
	Codec<IntSet> LINKED_INT_SET = KLibCodecs.setOf(Codec.INT).xmap(IntLinkedOpenHashSet::new, Function.identity());
	Codec<LongSet> LONG_SET = Codec.LONG_STREAM.xmap(LongOpenHashSet::toSet, LongCollection::longStream);

	static <K, V> Map<K, V> listToMap(List<V> list, Function<V, K> keyMapper) {
		if (list.isEmpty()) {
			return Map.of();
		}

		var map = new LinkedHashMap<K, V>(list.size());

		return map;
	}

	static <K, V> Codec<Map<K, V>> listToMap(Codec<V> codec, Function<V, K> keyMapper) {
		return codec.listOf().xmap(list -> listToMap(list, keyMapper), map -> map.isEmpty() ? List.of() : new ArrayList<>(map.values()));
	}

	static <V> Int2ObjectMap<V> listToInt2ObjectMap(List<V> list, ToIntFunction<V> keyMapper) {
		if (list.isEmpty()) {
			return Int2ObjectMaps.emptyMap();
		}

		Int2ObjectMap<V> map = new Int2ObjectLinkedOpenHashMap<>(list.size());

		for (V value : list) {
			map.put(keyMapper.applyAsInt(value), value);
		}

		return map;
	}

	static <V> Codec<Int2ObjectMap<V>> listToInt2ObjectMap(Codec<V> codec, ToIntFunction<V> keyMapper) {
		return codec.listOf().xmap(list -> listToInt2ObjectMap(list, keyMapper), map -> map.isEmpty() ? List.of() : new ArrayList<>(map.values()));
	}
}
