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

public record MD5(long a, long b) implements Checksum {
	public static final MD5 NIL = new MD5(0L, 0L);

	public static MD5 of(long a, long b) {
		if (a == 0L && b == 0L) {
			return NIL;
		} else {
			return new MD5(a, b);
		}
	}

	public static MD5 of(byte[] bytes) {
		var buf = ByteBuffer.wrap(bytes);
		var a = buf.getLong();
		var b = buf.getLong();
		return of(a, b);
	}

	public static final ChecksumType<MD5> TYPE = new ChecksumType<>(2, "md5", "MD5", NIL, 16, MD5::of);
	public static final Codec<MD5> CODEC = TYPE.codec;
	public static final StreamCodec<ByteBuf, MD5> STREAM_CODEC = TYPE.streamCodec;
	public static final DataType<MD5> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, MD5.class);

	@Override
	public ChecksumType<MD5> type() {
		return TYPE;
	}

	@Override
	@NotNull
	public String toString() {
		if (isNil()) {
			return "00000000000000000000000000000000";
		} else {
			return StringUtils.toHex(toByteArray());
		}
	}

	@Override
	public int hashCode() {
		return Long.hashCode(a ^ b);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof MD5 o && a == o.a && b == o.b;
	}

	@Override
	public boolean isNil() {
		return a == 0L && b == 0L;
	}

	@Override
	public byte[] toByteArray() {
		var bytes = ByteBuffer.allocate(16);
		bytes.putLong(a);
		bytes.putLong(b);
		return bytes.array();
	}

	@Override
	public void write(ByteOutput data) throws IOException {
		data.writeLong(a);
		data.writeLong(b);
	}

	@Override
	public void encode(ByteBuf buf) {
		buf.writeLong(a);
		buf.writeLong(b);
	}
}