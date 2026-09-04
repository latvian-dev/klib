package dev.latvian.mods.klib.io.bytes;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

public record ByteBufferByteInput(ByteBuffer in, int startingPosition) implements ByteInput {
	@Override
	public int readRaw() {
		if (in.hasRemaining()) {
			return in.get() & 0xFF;
		} else {
			return -1;
		}
	}

	@Override
	public void skip(long skip) throws IOException {
		long pos = in.position() + skip;

		if (pos > in.limit()) {
			throw new EOFException();
		}

		in.position((int) pos);
	}

	@Override
	public byte readByte() {
		return in.get();
	}

	@Override
	public int readAll(byte[] buffer, int offset, int len) {
		int result = Math.min(in.remaining(), len);
		in.get(buffer, offset, result);
		return result;
	}

	@Override
	public byte[] readAll() {
		var bytes = new byte[in.remaining()];
		in.get(bytes);
		return bytes;
	}

	@Override
	public short readShort() {
		return in.getShort();
	}

	@Override
	public int readUShort() {
		return in.getShort() & 0xFFFF;
	}

	@Override
	public int readInt() {
		return in.getInt();
	}

	@Override
	public long readLong() {
		return in.getLong();
	}

	@Override
	public float readFloat() {
		return in.getFloat();
	}

	@Override
	public double readDouble() {
		return in.getDouble();
	}
}