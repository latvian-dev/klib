package dev.latvian.mods.klib.io;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@FunctionalInterface
public interface ByteOutput {
	static OfStream of(OutputStream out) {
		return new OfStream(out);
	}

	static OfData of(DataOutput out) {
		return new OfData(out);
	}

	static OfBuffer of(ByteBuffer buf) {
		return new OfBuffer(buf, buf.position());
	}

	static OfData of(DataOutputStream out) {
		return new OfData(out);
	}

	static OfData ofByteBuilder(int initialSize) {
		return of(new ByteArrayDataOutputStream(new ByteArrayOutputStream(initialSize)));
	}

	record OfStream(OutputStream out) implements ByteOutput, Closeable {
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

	record OfData(DataOutput out) implements ByteOutput {
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

	record OfBuffer(ByteBuffer out, int startingPosition) implements ByteOutput {
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

	void writeByte(byte value) throws IOException;

	default void flush() throws IOException {
	}

	default void writeUByte(int value) throws IOException {
		writeByte((byte) value);
	}

	default void writeAll(byte[] value, int offset, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			writeByte(value[i + offset]);
		}
	}

	default void writeAll(byte[] value) throws IOException {
		writeAll(value, 0, value.length);
	}

	default void writeShort(short value) throws IOException {
		writeUShort(value & 0xFFFF);
	}

	default void writeUShort(int value) throws IOException {
		writeUByte((value >> 8) & 0xFF);
		writeUByte(value & 0xFF);
	}

	default void writeInt(int value) throws IOException {
		writeUByte((value >> 24) & 0xFF);
		writeUByte((value >> 16) & 0xFF);
		writeUByte((value >> 8) & 0xFF);
		writeUByte(value & 0xFF);
	}

	default void writeLong(long value) throws IOException {
		writeUByte((int) ((value >> 56L) & 0xFFL));
		writeUByte((int) ((value >> 48L) & 0xFFL));
		writeUByte((int) ((value >> 40L) & 0xFFL));
		writeUByte((int) ((value >> 32L) & 0xFFL));
		writeUByte((int) ((value >> 24L) & 0xFFL));
		writeUByte((int) ((value >> 16L) & 0xFFL));
		writeUByte((int) ((value >> 8L) & 0xFFL));
		writeUByte((int) (value & 0xFFL));
	}

	default void writeFloat(float value) throws IOException {
		writeInt(Float.floatToIntBits(value));
	}

	default void writeDouble(double value) throws IOException {
		writeLong(Double.doubleToLongBits(value));
	}

	default void writeVarInt(int value) throws IOException {
		while ((value & -128) != 0) {
			writeByte((byte) (value & 127 | 128));
			value >>>= 7;
		}

		writeByte((byte) value);
	}

	default void writeVarLong(long value) throws IOException {
		while ((value & -128L) != 0L) {
			writeByte((byte) ((int) (value & 127L) | 128));
			value >>>= 7;
		}

		writeByte((byte) value);
	}

	default void writeByteArray(byte[] value) throws IOException {
		writeVarInt(value.length);
		writeAll(value);
	}

	default void writeUTF(String value) throws IOException {
		writeByteArray(value.getBytes(StandardCharsets.UTF_8));
	}

	default void writeExactTime(@Nullable Instant value) throws IOException {
		if (value == null) {
			writeVarLong(0L);
			writeVarInt(0);
		} else {
			writeVarLong(value.getEpochSecond());
			writeVarInt(value.getNano());
		}
	}

	default void writeUUID(@Nullable UUID value) throws IOException {
		if (value == null) {
			writeLong(0L);
			writeLong(0L);
		} else {
			writeLong(value.getMostSignificantBits());
			writeLong(value.getLeastSignificantBits());
		}
	}

	default byte[] toByteArray() throws IOException {
		throw new UnsupportedOperationException("This ByteOutput does not support toByteArray()");
	}
}
