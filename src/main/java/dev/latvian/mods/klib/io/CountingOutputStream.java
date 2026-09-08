package dev.latvian.mods.klib.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream extends OutputStream {
	private long count = 0L;

	@Override
	public void write(int value) throws IOException {
		count++;
	}

	@Override
	public void write(byte @NotNull [] b) throws IOException {
		count += b.length;
	}

	@Override
	public void write(byte @NotNull [] b, int off, int len) throws IOException {
		count += len;
	}

	public long getCount() {
		return count;
	}
}
