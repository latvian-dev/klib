package dev.latvian.mods.klib.util;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@FunctionalInterface
public interface NameProvider<T> {
	static String provideDefaultName(Object o) {
		return switch (o) {
			case StringRepresentable s -> s.getSerializedName();
			case Enum<?> e -> e.name().toLowerCase(Locale.ROOT);
			default -> o.toString().toLowerCase(Locale.ROOT);
		};
	}

	NameProvider<?> DEFAULT = NameProvider::provideDefaultName;

	static <T> NameProvider<T> resolve(@Nullable NameProvider<T> provider) {
		return provider == null ? (NameProvider) DEFAULT : provider;
	}

	String provideName(T value);

	default Function<T, String> toFunction() {
		return this::provideName;
	}

	default List<Map.Entry<String, T>> toEntryList(T[] values) {
		var entries = new ArrayList<Map.Entry<String, T>>(values.length);

		for (var value : values) {
			entries.add(Map.entry(provideName(value), value));
		}

		return entries;
	}

	default List<Map.Entry<String, T>> toEntryList(Iterable<? extends T> values) {
		var entries = new ArrayList<Map.Entry<String, T>>();

		for (var value : values) {
			entries.add(Map.entry(provideName(value), value));
		}

		return entries;
	}
}
