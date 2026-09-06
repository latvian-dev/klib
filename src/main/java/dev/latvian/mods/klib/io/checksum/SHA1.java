package dev.latvian.mods.klib.io.checksum;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.io.bytes.ByteOutput;
import dev.latvian.mods.klib.util.StringUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;

public record SHA1(long a, long b, int c) implements Checksum {
	public static final SHA1 NIL = new SHA1(0L, 0L, 0);

	public static SHA1 of(long a, long b, int c) {
		if (a == 0L && b == 0L && c == 0) {
			return NIL;
		} else {
			return new SHA1(a, b, c);
		}
	}

	public static SHA1 of(byte[] bytes) {
		var buf = ByteBuffer.wrap(bytes);
		var a = buf.getLong();
		var b = buf.getLong();
		var c = buf.getInt();
		return of(a, b, c);
	}

	public static final ChecksumType<SHA1> TYPE = new ChecksumType<>(3, "sha1", "SHA-1", NIL, 20, SHA1::of);
	public static final Codec<SHA1> CODEC = TYPE.codec;
	public static final StreamCodec<ByteBuf, SHA1> STREAM_CODEC = TYPE.streamCodec;
	public static final DataType<SHA1> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, SHA1.class);

	@Override
	public ChecksumType<SHA1> type() {
		return TYPE;
	}

	@Override
	@NotNull
	public String toString() {
		if (isNil()) {
			return "0000000000000000000000000000000000000000";
		} else {
			return StringUtils.toHex(toByteArray());
		}
	}

	@Override
	public int hashCode() {
		return Long.hashCode(a ^ b ^ c);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof SHA1 o && a == o.a && b == o.b && c == o.c;
	}

	@Override
	public boolean isNil() {
		return a == 0L && b == 0L && c == 0;
	}

	@Override
	public byte[] toByteArray() {
		var bytes = ByteBuffer.allocate(type().size);
		bytes.putLong(a);
		bytes.putLong(b);
		bytes.putInt(c);
		return bytes.array();
	}

	@Override
	public void write(ByteOutput data) throws IOException {
		data.writeLong(a);
		data.writeLong(b);
		data.writeInt(c);
	}
}
