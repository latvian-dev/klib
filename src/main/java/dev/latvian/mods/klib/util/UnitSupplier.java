package dev.latvian.mods.klib.util;

import java.util.function.Supplier;

public record UnitSupplier<T>(T value) implements Supplier<T> {
	public static <T> UnitSupplier<T> of(T value) {
		return new UnitSupplier<>(value);
	}

	@Override
	public T get() {
		return value;
	}
}
