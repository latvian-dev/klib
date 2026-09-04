package dev.latvian.mods.klib.io.bytes;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public record DataByteInput(DataInput in) implements ByteInput {
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