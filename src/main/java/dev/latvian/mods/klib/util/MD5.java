package dev.latvian.mods.klib.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.io.ByteInput;
import dev.latvian.mods.klib.io.ByteOutput;
import dev.latvian.mods.klib.io.FileInfo;
import dev.latvian.mods.klib.io.IOUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.LongConsumer;

public record MD5(long most, long least) {
	public static final MD5 NIL = new MD5(0L, 0L);

	public static MD5 of(long most, long least) {
		if (most == 0L && least == 0L) {
			return NIL;
		} else {
			return new MD5(most, least);
		}
	}

	public static MD5 of(byte[] bytes) {
		var buf = ByteBuffer.wrap(bytes);
		var most = buf.getLong();
		var least = buf.getLong();
		return of(most, least);
	}

	public static MD5 of(String string) {
		if (string.isEmpty()) {
			return NIL;
		} else {
			var buf = ByteBuffer.wrap(StringUtils.fromHex(string));
			var most = buf.getLong();
			var least = buf.getLong();
			return of(most, least);
		}
	}

	public static MD5 read(ByteInput data) throws IOException {
		var most = data.readLong();
		var least = data.readLong();
		return of(most, least);
	}

	public static MD5 digest(byte[] input) throws NoSuchAlgorithmException {
		return of(MessageDigest.getInstance("MD5").digest(input));
	}

	public static final Codec<MD5> CODEC = Codec.STRING.comapFlatMap(string -> {
		try {
			return DataResult.success(of(string));
		} catch (Exception ex) {
			return DataResult.error(() -> "Invalid MD5 " + string);
		}
	}, md5 -> md5.isNil() ? "" : md5.toString());

	public static final StreamCodec<ByteBuf, MD5> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public MD5 decode(ByteBuf buf) {
			var most = buf.readLong();
			var least = buf.readLong();
			return of(most, least);
		}

		@Override
		public void encode(ByteBuf buf, MD5 value) {
			buf.writeLong(value.most);
			buf.writeLong(value.least);
		}
	};

	public static final DataType<MD5> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC);

	public static MD5 of(Path file, @Nullable LongConsumer callback) throws IOException {
		if (Files.exists(file)) {
			long size = Files.size(file);

			if (size > 0L) {
				return of(IOUtils.digest("MD5", file, size, callback));
			}
		}

		return NIL;
	}

	public static MD5 of(FileInfo fileInfo, @Nullable LongConsumer callback) throws IOException {
		if (fileInfo.size() > 0L && Files.exists(fileInfo.path())) {
			return of(IOUtils.digest("MD5", fileInfo.path(), fileInfo.size(), callback));
		}

		return NIL;
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
		return Long.hashCode(most ^ least);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof MD5 o && most == o.most && least == o.least;
	}

	public boolean isNil() {
		return most == 0L && least == 0L;
	}

	public void write(ByteOutput data) throws IOException {
		data.writeLong(most);
		data.writeLong(least);
	}

	public byte[] toByteArray() {
		var bytes = ByteBuffer.allocate(16);
		bytes.putLong(most);
		bytes.putLong(least);
		return bytes.array();
	}
}