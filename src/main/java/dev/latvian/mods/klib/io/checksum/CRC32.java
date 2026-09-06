package dev.latvian.mods.klib.io.checksum;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.io.IOUtils;
import dev.latvian.mods.klib.io.bytes.ByteOutput;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.LongConsumer;

public record CRC32(int a) implements Checksum {
	public static final CRC32 NIL = new CRC32(0);

	public static CRC32 of(int a) {
		if (a == 0) {
			return NIL;
		} else {
			return new CRC32(a);
		}
	}

	public static CRC32 of(byte[] bytes) {
		var buf = ByteBuffer.wrap(bytes);
		var a = buf.getInt();
		return of(a);
	}

	public static final ChecksumType<CRC32> TYPE = new ChecksumType<>(1, "crc32", "CRC32", NIL, 4, CRC32::of) {
		@Override
		public CRC32 digest(Path file, long offset, long size, @Nullable LongConsumer callback) throws IOException {
			if (size > 0L && Files.exists(file)) {
				var crc32 = new java.util.zip.CRC32();
				IOUtils.consumeFile(file, offset, size, callback, crc32::update);
				return CRC32.of((int) crc32.getValue());
			}

			return nil;
		}

		@Override
		public CRC32 digest(byte[] input, int offset, int len) {
			if (len > 0) {
				var crc32 = new java.util.zip.CRC32();
				crc32.update(input, offset, len);
				return CRC32.of((int) crc32.getValue());
			}

			return nil;
		}
	};

	public static final Codec<CRC32> CODEC = TYPE.codec;
	public static final StreamCodec<ByteBuf, CRC32> STREAM_CODEC = TYPE.streamCodec;
	public static final DataType<CRC32> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, CRC32.class);

	@Override
	public ChecksumType<CRC32> type() {
		return TYPE;
	}

	@Override
	@NotNull
	public String toString() {
		if (isNil()) {
			return "00000000";
		} else {
			return "%08x".formatted(a);
		}
	}

	@Override
	public int hashCode() {
		return a;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof CRC32 o && a == o.a;
	}

	@Override
	public boolean isNil() {
		return a == 0;
	}

	@Override
	public byte[] toByteArray() {
		var bytes = ByteBuffer.allocate(4);
		bytes.putInt(a);
		return bytes.array();
	}

	@Override
	public void write(ByteOutput data) throws IOException {
		data.writeInt(a);
	}
}