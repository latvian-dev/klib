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

public record SHA512(long a, long b, long c, long d, long e, long f, long g, long h) implements Checksum {
	public static final SHA512 NIL = new SHA512(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);

	public static SHA512 of(long a, long b, long c, long d, long e, long f, long g, long h) {
		if (a == 0L && b == 0L && c == 0L && d == 0L && e == 0L && f == 0L && g == 0L && h == 0L) {
			return NIL;
		} else {
			return new SHA512(a, b, c, d, e, f, g, h);
		}
	}

	public static SHA512 of(byte[] bytes) {
		var buf = ByteBuffer.wrap(bytes);
		var a = buf.getLong();
		var b = buf.getLong();
		var c = buf.getLong();
		var d = buf.getLong();
		var e = buf.getLong();
		var f = buf.getLong();
		var g = buf.getLong();
		var h = buf.getLong();
		return of(a, b, c, d, e, f, g, h);
	}

	public static final ChecksumType<SHA512> TYPE = new ChecksumType<>(6, "sha512", "SHA-512", NIL, 64, SHA512::of);
	public static final Codec<SHA512> CODEC = TYPE.codec;
	public static final StreamCodec<ByteBuf, SHA512> STREAM_CODEC = TYPE.streamCodec;
	public static final DataType<SHA512> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, SHA512.class);

	@Override
	public ChecksumType<SHA512> type() {
		return TYPE;
	}

	@Override
	@NotNull
	public String toString() {
		if (isNil()) {
			return "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
		} else {
			return StringUtils.toHex(toByteArray());
		}
	}

	@Override
	public int hashCode() {
		return Long.hashCode(a ^ b ^ c ^ d ^ e ^ f ^ g ^ h);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof SHA512 o && a == o.a && b == o.b && c == o.c && d == o.d && e == o.e && f == o.f && g == o.g && h == o.h;
	}

	@Override
	public boolean isNil() {
		return a == 0L && b == 0L && c == 0L && d == 0L && e == 0L && f == 0L && g == 0L && h == 0L;
	}

	@Override
	public byte[] toByteArray() {
		var bytes = ByteBuffer.allocate(64);
		bytes.putLong(a);
		bytes.putLong(b);
		bytes.putLong(c);
		bytes.putLong(d);
		bytes.putLong(e);
		bytes.putLong(f);
		bytes.putLong(g);
		bytes.putLong(h);
		return bytes.array();
	}

	@Override
	public void write(ByteOutput data) throws IOException {
		data.writeLong(a);
		data.writeLong(b);
		data.writeLong(c);
		data.writeLong(d);
		data.writeLong(e);
		data.writeLong(f);
		data.writeLong(g);
		data.writeLong(h);
	}

	@Override
	public void encode(ByteBuf buf) {
		buf.writeLong(a);
		buf.writeLong(b);
		buf.writeLong(c);
		buf.writeLong(d);
		buf.writeLong(e);
		buf.writeLong(f);
		buf.writeLong(g);
		buf.writeLong(h);
	}
}
