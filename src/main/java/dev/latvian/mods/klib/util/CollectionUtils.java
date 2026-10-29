package dev.latvian.mods.klib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SequencedCollection;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public interface CollectionUtils {
	static <T> SequencedCollection<T> toSequencedCollection(Iterable<T> collection) {
		if (collection instanceof SequencedCollection) {
			return (SequencedCollection<T>) collection;
		}

		var list = new ArrayList<T>();

		for (var element : collection) {
			list.add(element);
		}

		return list;
	}

	static int size(Iterable<?> iterable) {
		if (iterable instanceof Collection) {
			return ((Collection<?>) iterable).size();
		}

		int size = 0;

		for (var ignored : iterable) {
			size++;
		}

		return size;
	}

	static boolean isEmpty(Iterable<?> iterable) {
		if (iterable instanceof Collection) {
			return ((Collection<?>) iterable).isEmpty();
		}

		return iterable.iterator().hasNext();
	}

	static <T> T[] fastIndexedLookup(T[] values, ToIntFunction<T> idGetter, IntFunction<T[]> arrayConstructor) {
		int max = 0;

		for (var type : values) {
			max = Math.max(max, idGetter.applyAsInt(type));
		}

		var lookup = arrayConstructor.apply(max + 1);

		for (var type : values) {
			lookup[idGetter.applyAsInt(type)] = type;
		}

		return lookup;
	}
}
