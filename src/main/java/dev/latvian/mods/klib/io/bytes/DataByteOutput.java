package dev.latvian.mods.klib.io.bytes;

import dev.latvian.mods.klib.io.ByteArrayDataOutputStream;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

public record DataByteOutput(DataOutput out) implements ByteOutput {
	@Override
	public void writeByte(byte value) throws IOException {
		out.writeByte(value);
	}

	@Override
	public void flush() throws IOException {
		if (out instanceof OutputStream stream) {
			stream.flush();
		}
	}

	@Override
	public void writeUByte(int value) throws IOException {
		out.writeByte(value);
	}

	@Override
	public void writeAll(byte[] value, int offset, int len) throws IOException {
		out.write(value, offset, len);
	}

	@Override
	public void writeAll(byte[] value) throws IOException {
		out.write(value);
	}

	@Override
	public void writeShort(short value) throws IOException {
		out.writeShort(value);
	}

	@Override
	public void writeUShort(int value) throws IOException {
		out.writeShort(value);
	}

	@Override
	public void writeInt(int value) throws IOException {
		out.writeInt(value);
	}

	@Override
	public void writeLong(long value) throws IOException {
		out.writeLong(value);
	}

	@Override
	public void writeFloat(float value) throws IOException {
		out.writeFloat(value);
	}

	@Override
	public void writeDouble(double value) throws IOException {
		out.writeDouble(value);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		if (out instanceof ByteArrayDataOutputStream b) {
			return b.toByteArray();
		}

		return ByteOutput.super.toByteArray();
	}
}