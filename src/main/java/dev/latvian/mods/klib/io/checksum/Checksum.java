package dev.latvian.mods.klib.io.checksum;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.io.bytes.ByteBufByteOutput;
import dev.latvian.mods.klib.io.bytes.ByteInput;
import dev.latvian.mods.klib.io.bytes.ByteOutput;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.io.IOException;

public interface Checksum {
	static Checksum read(ByteInput data) throws IOException {
		var checksumType = ChecksumType.typeOf(data.readUByte());
		return checksumType.read(data);
	}

	static Checksum of(String checksum) {
		if (checksum.isEmpty()) {
			return NoChecksum.INSTANCE;
		}

		int len = checksum.length() / 2;

		for (var type : ChecksumType.TYPES) {
			if (type.size == len) {
				return type.of(checksum);
			}
		}

		throw new IllegalArgumentException("Unknown checksum algorithm of " + checksum);
	}

	Codec<Checksum> CODEC = Codec.STRING.comapFlatMap(checksum -> {
		if (checksum.isEmpty()) {
			return DataResult.success(NoChecksum.INSTANCE);
		}

		int len = checksum.length() / 2;

		for (var type : ChecksumType.TYPES) {
			if (type.size == len) {
				return DataResult.success(type.of(checksum));
			}
		}

		return DataResult.error(() -> "Unknown checksum algorithm of " + checksum);
	}, Checksum::toString);

	StreamCodec<ByteBuf, Checksum> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public Checksum decode(ByteBuf buf) {
			var type = ChecksumType.typeOf(buf.readUnsignedByte());
			return type.streamCodec.decode(buf);
		}

		@Override
		public void encode(ByteBuf buf, Checksum checksum) {
			buf.writeByte(checksum.type().id);

			try {
				checksum.write(ByteBufByteOutput.of(buf));
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	};

	DataType<Checksum> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, Checksum.class);

	ChecksumType<?> type();

	boolean isNil();

	byte[] toByteArray();

	default void write(ByteOutput data) throws IOException {
		data.writeAll(toByteArray());
	}

	default void writeFully(ByteOutput data) throws IOException {
		data.writeUByte(type().id);
		write(data);
	}
}
