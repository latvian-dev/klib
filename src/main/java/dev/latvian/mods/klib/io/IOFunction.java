package dev.latvian.mods.klib.io;

import java.io.IOException;

@FunctionalInterface
public interface IOFunction<T, R> {
	R apply(T value) throws IOException;
}
