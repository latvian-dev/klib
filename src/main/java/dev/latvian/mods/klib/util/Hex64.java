package dev.latvian.mods.klib.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record Hex64(long raw) {
	public static final Hex64 NONE = new Hex64(0);

	public static Hex64 of(long id) {
		return id == 0L ? NONE : new Hex64(id);
	}

	public static Hex64 of(String id) {
		return id.length() != 16 ? NONE : of(Long.parseUnsignedLong(id, 16));
	}

	public static final Codec<Hex64> STRING_CODEC = Codec.STRING.comapFlatMap(id -> {
		if (id.length() == 16) {
			try {
				return DataResult.success(of(Long.parseUnsignedLong(id, 16)));
			} catch (Exception ignored) {
			}
		}

		return DataResult.error(() -> "Invalid hex ID '" + id + "'");
	}, Hex64::toString);
	public static final Codec<Hex64> LONG_CODEC = Codec.LONG.xmap(Hex64::of, Hex64::raw);
	public static final Codec<Hex64> CODEC = KLibCodecs.or(STRING_CODEC, LONG_CODEC);
	public static final StreamCodec<ByteBuf, Hex64> STREAM_CODEC = ByteBufCodecs.LONG.map(Hex64::of, Hex64::raw);
	public static final DataType<Hex64> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, Hex64.class);

	@Override
	public int hashCode() {
		return Long.hashCode(raw);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof Hex64 o && raw == o.raw;
	}

	@Override
	public @NotNull String toString() {
		return "%016X".formatted(raw);
	}

	public int getMostSignificantBits() {
		return (int) ((raw >> 32L) & 0xFFFFFFFFL);
	}

	public int getLeastSignificantBits() {
		return (int) (raw & 0xFFFFFFFFL);
	}
}
