package dev.latvian.mods.klib.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.io.IOUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.LongConsumer;
import java.util.zip.CRC32;

public record Hex32(int raw) {
	public static final Hex32 NONE = new Hex32(0);

	public static Hex32 of(int id) {
		return id == 0 ? NONE : new Hex32(id);
	}

	public static Hex32 of(String id) {
		return id.length() != 8 ? NONE : of(Integer.parseUnsignedInt(id, 16));
	}

	public static final Codec<Hex32> STRING_CODEC = Codec.STRING.comapFlatMap(id -> {
		if (id.length() == 8) {
			try {
				return DataResult.success(of(Integer.parseUnsignedInt(id, 16)));
			} catch (Exception ignored) {
			}
		}

		return DataResult.error(() -> "Invalid hex ID '" + id + "'");
	}, Hex32::toString);
	public static final Codec<Hex32> INT_CODEC = Codec.INT.xmap(Hex32::of, Hex32::raw);
	public static final Codec<Hex32> CODEC = KLibCodecs.or(STRING_CODEC, INT_CODEC);
	public static final StreamCodec<ByteBuf, Hex32> STREAM_CODEC = ByteBufCodecs.INT.map(Hex32::of, Hex32::raw);
	public static final DataType<Hex32> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, Hex32.class);

	public static Hex32 crc32(Path file, @Nullable LongConsumer callback) throws IOException {
		var crc32 = new CRC32();

		try (var channel = Files.newByteChannel(file)) {
			var buf = IOUtils.allocateTempBuffer(file);
			int len;

			while ((len = channel.read(buf)) != -1) {
				buf.flip();
				crc32.update(buf);
				buf.clear();

				if (callback != null) {
					callback.accept(len);
				}
			}

			return of((int) crc32.getValue());
		}
	}

	@Override
	public int hashCode() {
		return raw;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof Hex32 o && raw == o.raw;
	}

	@Override
	public @NotNull String toString() {
		return "%08X".formatted(raw);
	}
}
