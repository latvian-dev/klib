package dev.latvian.mods.klib.io;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

public class CountingOutputStream extends OutputStream {
	private long count = 0L;

	@Override
	public void write(int value) {
		count++;
	}

	@Override
	public void write(byte @NotNull [] b) {
		count += b.length;
	}

	@Override
	public void write(byte @NotNull [] b, int off, int len) {
		count += len;
	}

	public long getCount() {
		return count;
	}
}
