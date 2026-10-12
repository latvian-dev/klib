package dev.latvian.mods.klib.io.bytes;

import dev.latvian.mods.klib.io.ByteArrayDataOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public record StreamByteOutput(OutputStream out) implements ByteOutput, Closeable {
	@Override
	public void writeByte(byte value) throws IOException {
		out.write(value);
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void writeUByte(int value) throws IOException {
		out.write(value);
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
	public void close() throws IOException {
		out.close();
	}

	@Override
	public byte[] toByteArray() throws IOException {
		if (out instanceof ByteArrayOutputStream b) {
			return b.toByteArray();
		} else if (out instanceof ByteArrayDataOutputStream b) {
			return b.toByteArray();
		} else {
			return ByteOutput.super.toByteArray();
		}
	}
}