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
import java.util.List;

public interface Checksum {
	List<ChecksumType<?>> TYPES = List.of(
		NoChecksumType.TYPE,
		CRC32.TYPE,
		MD5.TYPE,
		SHA1.TYPE,
		SHA256.TYPE,
		SHA384.TYPE,
		SHA512.TYPE
	);

	static ChecksumType<?> typeOf(int id) {
		for (var type : TYPES) {
			if (type.id == id) {
				return type;
			}
		}

		throw new IllegalArgumentException("Unknown type: " + id);
	}

	static Checksum read(ByteInput data) throws IOException {
		var checksumType = typeOf(data.readUByte());
		return checksumType.read(data);
	}

	static Checksum of(String checksum) {
		if (checksum.isEmpty()) {
			return NoChecksum.INSTANCE;
		}

		int len = checksum.length() / 2;

		for (var type : TYPES) {
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

		for (var type : TYPES) {
			if (type.size == len) {
				return DataResult.success(type.of(checksum));
			}
		}

		return DataResult.error(() -> "Unknown checksum algorithm of " + checksum);
	}, Checksum::toString);

	StreamCodec<ByteBuf, Checksum> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public Checksum decode(ByteBuf buf) {
			var type = typeOf(buf.readUnsignedByte());
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
