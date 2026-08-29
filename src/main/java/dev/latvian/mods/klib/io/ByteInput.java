package dev.latvian.mods.klib.io;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
	static ByteInput of(InputStream in) {
		return new OfStream(in);
	}

	static ByteInput of(DataInput in) {
		return new OfData(in);
	}

	static ByteInput of(ByteBuffer in) {
		return new OfBuffer(in, in.position());
	}

	static ByteInput of(DataInputStream in) {
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

	record OfData(DataInput in) implements ByteInput {
		@Override
		public int readRaw() throws IOException {
			if (in instanceof InputStream stream) {
				return stream.read();
			} else {
				try {
					return in.readUnsignedByte();
				} catch (EOFException ex) {
					return -1;
				}
			}
		}

		@Override
		public void skip(long skip) throws IOException {
			if (in instanceof InputStream stream) {
				stream.skipNBytes(skip);
			} else {
				while (skip > 0L) {
					skip -= in.skipBytes((int) Math.min(skip, Integer.MAX_VALUE));
				}
			}
		}

		@Override
		public byte readByte() throws IOException {
			return in.readByte();
		}

		@Override
		public int readUByte() throws IOException {
			return in.readUnsignedByte();
		}

		@Override
		public int readAll(byte[] buffer, int offset, int len) throws IOException {
			if (in instanceof InputStream stream) {
				return stream.readNBytes(buffer, offset, len);
			} else {
				return ByteInput.super.readAll(buffer, offset, len);
			}
		}

		@Override
		public int readAll(byte[] buffer) throws IOException {
			if (in instanceof InputStream stream) {
				return stream.readNBytes(buffer, 0, buffer.length);
			} else {
				return ByteInput.super.readAll(buffer, 0, buffer.length);
			}
		}

		@Override
		public byte[] readAll() throws IOException {
			if (in instanceof InputStream stream) {
				return stream.readAllBytes();
			} else {
				var bytes = new ByteArrayOutputStream();

				try {
					while (true) {
						var raw = in.readUnsignedByte();
					}
				} catch (EOFException ex) {
					return bytes.toByteArray();
				}
			}
		}

		@Override
		public boolean readBoolean() throws IOException {
			return in.readBoolean();
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

	int readRaw() throws IOException;

	default void skip(long skip) throws IOException {
		while (skip > 0L) {
			readByte();
			skip--;
		}
	}

	default byte readByte() throws IOException {
		var value = readRaw();

		if (value == -1) {
			throw new EOFException();
		} else {
			return (byte) value;
		}
	}

	default int readUByte() throws IOException {
		var value = readRaw();

		if (value == -1) {
			throw new EOFException();
		} else {
			return value;
		}
	}

	default int readAll(byte[] buffer, int offset, int len) throws IOException {
		for (var i = 0; i < len; i++) {
			int value = readRaw();

			if (value == -1) {
				return i;
			} else {
				buffer[i + offset] = (byte) value;
			}
		}

		return len;
	}

	default int readAll(byte[] buffer) throws IOException {
		return readAll(buffer, 0, buffer.length);
	}

	default byte[] readAll() throws IOException {
		var bytes = new ByteArrayOutputStream();

		while (true) {
			var raw = readRaw();

			if (raw == -1) {
				return bytes.toByteArray();
			} else {
				bytes.write(raw);
			}
		}
	}

	default boolean readBoolean() throws IOException {
		return readByte() != 0;
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
