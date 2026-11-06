package dev.latvian.mods.klib.util;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public interface CollectionUtils {
	static <T> List<T> toList(Iterable<T> iterable) {
		if (iterable instanceof List<?> list) {
			return (List<T>) list;
		} else if (iterable instanceof Collection collection) {
			var list = new ArrayList<T>(collection.size());
			list.addAll(collection);
			return list;
		} else {
			var list = new ArrayList<T>();

			for (var element : iterable) {
				list.add(element);
			}

			return list;
		}
	}

	static <T> SequencedCollection<T> toSequencedCollection(Iterable<T> iterable) {
		if (iterable instanceof SequencedCollection<?> sequencedCollection) {
			return (SequencedCollection<T>) sequencedCollection;
		} else if (iterable instanceof Collection collection) {
			var list = new ArrayList<T>(collection.size());
			list.addAll(collection);
			return list;
		} else {
			var list = new ArrayList<T>();

			for (var element : iterable) {
				list.add(element);
			}

			return list;
		}
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

	static <T> List<T> join(List<T> list1, List<T> list2) {
		if (list1.isEmpty()) {
			return list2;
		} else if (list2.isEmpty()) {
			return list1;
		} else {
			List<T> list = new ArrayList<>(list1.size() + list2.size());
			list.addAll(list1);
			list.addAll(list2);
			return list;
		}
	}

	static <T> Set<T> join(Set<T> set1, Set<T> set2, boolean identity) {
		if (set1.isEmpty()) {
			return set2;
		} else if (set2.isEmpty()) {
			return set1;
		} else {
			Set<T> set = identity ? new ReferenceOpenHashSet<>(set1.size() + set2.size()) : new ObjectOpenHashSet<>(set1.size() + set2.size());
			set.addAll(set1);
			set.addAll(set2);
			return set;
		}
	}

	static <T> Iterable<T> join(Iterable<T> iterable1, Iterable<T> iterable2) {
		if (iterable1 instanceof Collection<?> c && c.isEmpty()) {
			return iterable2;
		} else if (iterable2 instanceof Collection<?> c && c.isEmpty()) {
			return iterable1;
		} else {
			return Iterables.concat(iterable1, iterable2);
		}
	}
}
