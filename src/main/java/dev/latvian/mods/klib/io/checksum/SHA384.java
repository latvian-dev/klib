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

public record SHA384(long a, long b, long c, long d, long e, long f) implements Checksum {
	public static final SHA384 NIL = new SHA384(0L, 0L, 0L, 0L, 0L, 0L);

	public static SHA384 of(long a, long b, long c, long d, long e, long f) {
		if (a == 0L && b == 0L && c == 0L && d == 0L && e == 0L && f == 0L) {
			return NIL;
		} else {
			return new SHA384(a, b, c, d, e, f);
		}
	}

	public static SHA384 of(byte[] bytes) {
		var buf = ByteBuffer.wrap(bytes);
		var a = buf.getLong();
		var b = buf.getLong();
		var c = buf.getLong();
		var d = buf.getLong();
		var e = buf.getLong();
		var f = buf.getLong();
		return of(a, b, c, d, e, f);
	}

	public static final ChecksumType<SHA384> TYPE = new ChecksumType<>(5, "sha384", "SHA-384", NIL, 48, SHA384::of);
	public static final Codec<SHA384> CODEC = TYPE.codec;
	public static final StreamCodec<ByteBuf, SHA384> STREAM_CODEC = TYPE.streamCodec;
	public static final DataType<SHA384> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, SHA384.class);

	@Override
	public ChecksumType<SHA384> type() {
		return TYPE;
	}

	@Override
	@NotNull
	public String toString() {
		if (isNil()) {
			return "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
		} else {
			return StringUtils.toHex(toByteArray());
		}
	}

	@Override
	public int hashCode() {
		return Long.hashCode(a ^ b ^ c ^ d ^ e ^ f);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof SHA384 o && a == o.a && b == o.b && c == o.c && d == o.d && e == o.e && f == o.f;
	}

	@Override
	public boolean isNil() {
		return a == 0L && b == 0L && c == 0L && d == 0L && e == 0L && f == 0L;
	}

	@Override
	public byte[] toByteArray() {
		var bytes = ByteBuffer.allocate(type().size);
		bytes.putLong(a);
		bytes.putLong(b);
		bytes.putLong(c);
		bytes.putLong(d);
		bytes.putLong(e);
		bytes.putLong(f);
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
	}
}
