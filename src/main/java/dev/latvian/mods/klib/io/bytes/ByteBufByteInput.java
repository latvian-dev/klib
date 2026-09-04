package dev.latvian.mods.klib.io.bytes;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.VarLong;

public record ByteBufByteInput(ByteBuf in, int startingPosition) implements ByteInput {
	public static ByteBufByteInput of(ByteBuf in) {
		return new ByteBufByteInput(in, in.readerIndex());
	}

	@Override
	public int readRaw() {
		try {
			return in.readUnsignedByte() & 0xFF;
		} catch (IndexOutOfBoundsException ex) {
			return -1;
		}
	}

	@Override
	public void skip(long skip) {
		while (skip > Integer.MAX_VALUE) {
			in.skipBytes(Integer.MAX_VALUE);
			skip -= Integer.MAX_VALUE;
		}

		in.skipBytes((int) skip);
	}

	@Override
	public byte readByte() {
		return in.readByte();
	}

	@Override
	public int readAll(byte[] buffer, int offset, int len) {
		in.readBytes(buffer, offset, len);
		return len;
	}

	@Override
	public byte[] readAll() {
		var bytes = new byte[in.readableBytes()];
		in.readBytes(bytes);
		return bytes;
	}

	@Override
	public short readShort() {
		return in.readShort();
	}

	@Override
	public int readUShort() {
		return in.readUnsignedShort();
	}

	@Override
	public int readInt() {
		return in.readInt();
	}

	@Override
	public long readLong() {
		return in.readLong();
	}

	@Override
	public float readFloat() {
		return in.readFloat();
	}

	@Override
	public double readDouble() {
		return in.readDouble();
	}

	@Override
	public int readVarInt() {
		return VarInt.read(in);
	}

	@Override
	public long readVarLong() {
		return VarLong.read(in);
	}
}