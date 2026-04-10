package dev.latvian.mods.klib.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public record MD5(byte[] bytes, String string) {
	public static final MD5 NIL = new MD5(new byte[16], "00000000000000000000000000000000");

	private static boolean isNIL(byte[] bytes) {
		for (byte b : bytes) {
			if (b != 0) {
				return false;
			}
		}

		return true;
	}

	public static MD5 fromBytes(byte[] bytes) {
		return isNIL(bytes) ? NIL : new MD5(bytes, StringUtils.toHex(bytes));
	}

	public static MD5 fromString(String string) {
		if (string.isEmpty()) {
			return NIL;
		} else if (string.contains("/") || string.contains("\\") || string.contains("..")) {
			throw new IllegalStateException("Invalid checksum");
		} else {
			var bytes = StringUtils.fromHex(string);
			return isNIL(bytes) ? NIL : new MD5(bytes, string);
		}
	}

	public static MD5 read(DataInput data) throws IOException {
		var bytes = new byte[16];
		data.readFully(bytes);
		return fromBytes(bytes);
	}

	public static final Codec<MD5> CODEC = Codec.STRING.comapFlatMap(string -> {
		try {
			return DataResult.success(fromString(string));
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid MD5 " + string);
		}
	}, md5 -> md5.isEmpty() ? "" : md5.string());

	public static final StreamCodec<ByteBuf, MD5> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public MD5 decode(ByteBuf buf) {
			var bytes = new byte[16];
			buf.readBytes(bytes);
			return fromBytes(bytes);
		}

		@Override
		public void encode(ByteBuf buf, MD5 value) {
			buf.writeBytes(value.bytes());
		}
	};

	public static final DataType<MD5> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, MD5.class);

	@Override
	public @NotNull String toString() {
		return string;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(bytes);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof MD5 o && Arrays.equals(bytes, o.bytes);
	}

	public boolean isEmpty() {
		return this == NIL;
	}

	public void write(DataOutput data) throws IOException {
		data.write(bytes);
	}
}
