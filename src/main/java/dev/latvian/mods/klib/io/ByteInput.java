package dev.latvian.mods.klib.io;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@FunctionalInterface
public interface ByteInput {
	static OfStream of(InputStream in) {
		return new OfStream(in);
	}

	static OfData of(DataInput in) {
		return new OfData(in);
	}

	static OfBuffer of(ByteBuffer in) {
		return new OfBuffer(in, in.position());
	}

	static OfData of(DataInputStream in) {
		return new OfData(in);
	}

	static ByteInput of(byte[] bytes) {
		if (bytes.length > 16) {
			try {
				return of(new DataInputStream(new ByteArrayInputStream(bytes)));
			} catch (Exception ex) {
				return of(new ByteArrayInputStream(bytes));
			}
		} else {
			return of(ByteBuffer.wrap(bytes));
		}
	}

	record OfStream(InputStream in) implements ByteInput {
		@Override
		public byte readByte() throws IOException {
			var value = in.read();

			if (value == -1) {
				throw new EOFException();
			} else {
				return (byte) value;
			}
		}

		@Override
		public int readUByte() throws IOException {
			var value = in.read();

			if (value == -1) {
				throw new EOFException();
			} else {
				return value;
			}
		}

		@Override
		public void readAll(byte[] buffer, int offset, int len) throws IOException {
			in.readNBytes(buffer, offset, len);
		}
	}

	record OfData(DataInput in) implements ByteInput {
		@Override
		public byte readByte() throws IOException {
			return in.readByte();
		}

		@Override
		public int readUByte() throws IOException {
			return in.readUnsignedByte();
		}

		@Override
		public void readAll(byte[] buffer, int offset, int len) throws IOException {
			in.readFully(buffer, offset, len);
		}

		@Override
		public void readAll(byte[] buffer) throws IOException {
			in.readFully(buffer);
		}

		@Override
		public short readShort() throws IOException {
			return in.readShort();
		}

		@Override
		public int readUShort() throws IOException {
			return in.readUnsignedShort();
		}

		@Override
		public int readInt() throws IOException {
			return in.readInt();
		}

		@Override
		public long readLong() throws IOException {
			return in.readLong();
		}

		@Override
		public float readFloat() throws IOException {
			return in.readFloat();
		}

		@Override
		public double readDouble() throws IOException {
			return in.readDouble();
		}
	}

	record OfBuffer(ByteBuffer in, int startingPosition) implements ByteInput {
		@Override
		public byte readByte() {
			return in.get();
		}

		@Override
		public void readAll(byte[] buffer, int offset, int len) {
			in.get(buffer, offset, len);
		}

		@Override
		public void readAll(byte[] buffer) {
			in.get(buffer);
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

	byte readByte() throws IOException;

	default int readUByte() throws IOException {
		return readByte() & 0xFF;
	}

	default void readAll(byte[] buffer, int offset, int len) throws IOException {
		for (var i = 0; i < len; i++) {
			buffer[i + offset] = readByte();
		}
	}

	default void readAll(byte[] buffer) throws IOException {
		readAll(buffer, 0, buffer.length);
	}

	default short readShort() throws IOException {
		return (short) readUShort();
	}

	default int readUShort() throws IOException {
		return (readUByte() << 8) | readUByte();
	}

	default int readInt() throws IOException {
		return (readUByte() << 24) | (readUByte() << 16) | (readUByte() << 8) | readUByte();
	}

	default long readLong() throws IOException {
		return ((long) readUByte() << 56L) | ((long) readUByte() << 48L) | ((long) readUByte() << 40L) | ((long) readUByte() << 32L) | ((long) readUByte() << 24L) | ((long) readUByte() << 16L) | ((long) readUByte() << 8L) | ((long) readUByte());
	}

	default float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	default double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	default byte[] readByteArray() throws IOException {
		int length = readVarInt();
		var bytes = new byte[length];
		readAll(bytes);
		return bytes;
	}

	default int readVarInt() throws IOException {
		int value = 0;
		int count = 0;

		byte b0;
		do {
			b0 = readByte();
			value |= (b0 & 127) << count++ * 7;

			if (count > 5) {
				throw new IOException("VarInt too big");
			}
		} while ((b0 & 128) == 128);

		return value;
	}

	default long readVarLong() throws IOException {
		long value = 0L;
		int count = 0;

		byte b0;
		do {
			b0 = readByte();
			value |= (long) (b0 & 127) << count++ * 7;

			if (count > 10) {
				throw new IOException("VarLong too big");
			}
		} while ((b0 & 128) == 128);

		return value;
	}

	default String readUTF() throws IOException {
		return new String(readByteArray(), StandardCharsets.UTF_8);
	}

	default Instant readExactTime() throws IOException {
		var second = readVarLong();
		var nano = readVarInt();
		return second == 0L && nano == 0L ? Instant.EPOCH : Instant.ofEpochSecond(second, nano);
	}

	@Nullable
	default Instant readNullableExactTime() throws IOException {
		var second = readVarLong();
		var nano = readVarInt();
		return second == 0L && nano == 0L ? null : Instant.ofEpochSecond(second, nano);
	}

	default UUID readUUID() throws IOException {
		var most = readLong();
		var least = readLong();
		return most == 0L && least == 0L ? IOUtils.NIL_UUID : new UUID(most, least);
	}

	@Nullable
	default UUID readNullableUUID() throws IOException {
		var most = readLong();
		var least = readLong();
		return most == 0L && least == 0L ? null : new UUID(most, least);
	}
}
