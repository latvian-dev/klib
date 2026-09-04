package dev.latvian.mods.klib.io.bytes;

import java.io.IOException;
import java.nio.ByteBuffer;

public record ByteBufferByteOutput(ByteBuffer out, int startingPosition) implements ByteOutput {
	@Override
	public void writeByte(byte value) {
		out.put(value);
	}

	@Override
	public void writeAll(byte[] value, int offset, int len) {
		out.put(value, offset, len);
	}

	@Override
	public void writeAll(byte[] value) {
		out.put(value);
	}

	@Override
	public void writeShort(short value) {
		out.putShort(value);
	}

	@Override
	public void writeUShort(int value) {
		out.putShort((short) value);
	}

	@Override
	public void writeInt(int value) {
		out.putInt(value);
	}

	@Override
	public void writeLong(long value) {
		out.putLong(value);
	}

	@Override
	public void writeFloat(float value) {
		out.putFloat(value);
	}

	@Override
	public void writeDouble(double value) {
		out.putDouble(value);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		int pos = out.position();
		var bytes = new byte[pos - startingPosition];
		out.get(startingPosition, bytes);
		return bytes;
	}
}