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

public record SHA256(long a, long b, long c, long d) implements Checksum {
	public static final SHA256 NIL = new SHA256(0L, 0L, 0L, 0L);

	public static SHA256 of(long a, long b, long c, long d) {
		if (a == 0L && b == 0L && c == 0L && d == 0L) {
			return NIL;
		} else {
			return new SHA256(a, b, c, d);
		}
	}

	public static SHA256 of(byte[] bytes) {
		var buf = ByteBuffer.wrap(bytes);
		var a = buf.getLong();
		var b = buf.getLong();
		var c = buf.getLong();
		var d = buf.getLong();
		return of(a, b, c, d);
	}

	public static final ChecksumType<SHA256> TYPE = new ChecksumType<>(4, "sha256", "SHA-256", NIL, 32, SHA256::of);
	public static final Codec<SHA256> CODEC = TYPE.codec;
	public static final StreamCodec<ByteBuf, SHA256> STREAM_CODEC = TYPE.streamCodec;
	public static final DataType<SHA256> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, SHA256.class);

	@Override
	public ChecksumType<SHA256> type() {
		return TYPE;
	}

	@Override
	@NotNull
	public String toString() {
		if (isNil()) {
			return "0000000000000000000000000000000000000000000000000000000000000000";
		} else {
			return StringUtils.toHex(toByteArray());
		}
	}

	@Override
	public int hashCode() {
		return Long.hashCode(a ^ b ^ c ^ d);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof SHA256 o && a == o.a && b == o.b && c == o.c && d == o.d;
	}

	@Override
	public boolean isNil() {
		return a == 0L && b == 0L && c == 0L && d == 0L;
	}

	@Override
	public byte[] toByteArray() {
		var bytes = ByteBuffer.allocate(64);
		bytes.putLong(a);
		bytes.putLong(b);
		bytes.putLong(c);
		bytes.putLong(d);
		return bytes.array();
	}

	@Override
	public void write(ByteOutput data) throws IOException {
		data.writeLong(a);
		data.writeLong(b);
		data.writeLong(c);
		data.writeLong(d);
	}
}
