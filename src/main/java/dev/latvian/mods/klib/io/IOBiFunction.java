package dev.latvian.mods.klib.io;

import java.io.IOException;

@FunctionalInterface
public interface IOBiFunction<T, U, R> {
	R apply(T value1, U value2) throws IOException;
}
