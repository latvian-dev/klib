package dev.latvian.mods.klib.io.bytes;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.VarLong;

import java.io.IOException;

public record ByteBufByteOutput(ByteBuf out, int startingPosition) implements ByteOutput {
	public static ByteBufByteOutput of(ByteBuf buf) {
		return new ByteBufByteOutput(buf, buf.writerIndex());
	}

	@Override
	public void writeByte(byte value) throws IOException {
		out.writeByte(value);
	}

	@Override
	public void writeUByte(int value) {
		out.writeByte(value);
	}

	@Override
	public void writeAll(byte[] value, int offset, int len) {
		out.writeBytes(value, offset, len);
	}

	@Override
	public void writeAll(byte[] value) {
		out.writeBytes(value);
	}

	@Override
	public void writeShort(short value) {
		out.writeShort(value);
	}

	@Override
	public void writeUShort(int value) {
		out.writeShort(value);
	}

	@Override
	public void writeInt(int value) {
		out.writeInt(value);
	}

	@Override
	public void writeLong(long value) {
		out.writeLong(value);
	}

	@Override
	public void writeFloat(float value) {
		out.writeFloat(value);
	}

	@Override
	public void writeDouble(double value) {
		out.writeDouble(value);
	}

	@Override
	public void writeVarInt(int value) {
		VarInt.write(out, value);
	}

	@Override
	public void writeVarLong(long value) {
		VarLong.write(out, value);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		int pos = out.writerIndex();
		var bytes = new byte[pos - startingPosition];
		out.getBytes(startingPosition, bytes);
		return bytes;
	}
}