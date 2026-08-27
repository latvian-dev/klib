package dev.latvian.mods.klib.io;

import java.io.IOException;
import java.util.Arrays;

@FunctionalInterface
public interface IOByteArrayOperator<T> {
	IOByteArrayOperator<byte[]> IDENTITY = (data, offset, len) -> {
		if (offset == 0 && len == data.length) {
			return data;
		} else {
			return Arrays.copyOfRange(data, offset, len);
		}
	};

	T apply(byte[] data, int offset, int len) throws IOException;
}
