package dev.latvian.mods.klib.io.bytes;

import dev.latvian.mods.klib.io.IOUtils;
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
		return new StreamByteInput(in);
	}

	static ByteInput of(DataInput in) {
		return new DataByteInput(in);
	}

	static ByteInput of(ByteBuffer in) {
		return new ByteBufferByteInput(in, in.position());
	}

	static ByteInput of(DataInputStream in) {
		return new DataByteInput(in);
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
