package dev.latvian.mods.klib.io.checksum;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.io.bytes.ByteBufByteOutput;
import dev.latvian.mods.klib.io.bytes.ByteInput;
import dev.latvian.mods.klib.io.bytes.ByteOutput;
import dev.latvian.mods.klib.util.Cast;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.io.IOException;

public interface Checksum {
	static Checksum read(ByteInput data) throws IOException {
		var checksumType = ChecksumType.typeOf(data.readUByte());
		return checksumType.read(data);
	}

	Codec<Checksum> CODEC = KLibCodecs.or(NoChecksum.TYPE.codec, KLibCodecs.or(Cast.to(ChecksumType.TYPES.stream().filter(t -> t != NoChecksum.TYPE).map(t -> t.codec).toList())));

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
