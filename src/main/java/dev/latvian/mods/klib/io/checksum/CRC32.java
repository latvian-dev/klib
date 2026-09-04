package dev.latvian.mods.klib.io.checksum;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.io.FileInfo;
import dev.latvian.mods.klib.io.IOUtils;
import dev.latvian.mods.klib.io.bytes.ByteOutput;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
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
		public CRC32 digest(FileInfo file, @Nullable LongConsumer callback) throws IOException {
			var crc32 = new java.util.zip.CRC32();

			try (var channel = Files.newByteChannel(file.path())) {
				var buf = IOUtils.allocateTempBuffer(16384, file.size());
				int len;

				while ((len = channel.read(buf)) != -1) {
					buf.flip();
					crc32.update(buf);
					buf.clear();

					if (callback != null) {
						callback.accept(len);
					}
				}

				return CRC32.of((int) crc32.getValue());
			}
		}

		@Override
		public CRC32 digest(byte[] input) {
			var crc32 = new java.util.zip.CRC32();
			crc32.update(input);
			return CRC32.of((int) crc32.getValue());
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

	@Override
	public void encode(ByteBuf buf) {
		buf.writeInt(a);
	}
}