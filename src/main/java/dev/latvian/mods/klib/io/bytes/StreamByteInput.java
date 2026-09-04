package dev.latvian.mods.klib.io.bytes;

import java.io.IOException;
import java.io.InputStream;

public record StreamByteInput(InputStream in) implements ByteInput {
	@Override
	public int readRaw() throws IOException {
		return in.read();
	}

	@Override
	public void skip(long skip) throws IOException {
		in.skipNBytes(skip);
	}

	@Override
	public int readAll(byte[] buffer, int offset, int len) throws IOException {
		return in.readNBytes(buffer, offset, len);
	}

	@Override
	public byte[] readAll() throws IOException {
		return in.readAllBytes();
	}
}