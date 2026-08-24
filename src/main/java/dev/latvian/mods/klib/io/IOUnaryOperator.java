package dev.latvian.mods.klib.io;

import java.io.IOException;

@FunctionalInterface
public interface IOUnaryOperator<T> {
	T apply(T value) throws IOException;
}
