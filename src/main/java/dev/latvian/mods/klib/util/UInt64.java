package dev.latvian.mods.klib.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record UInt64(long raw) {
	public static final UInt64 NONE = new UInt64(0L);

	public static UInt64 of(long id) {
		return id == 0L ? NONE : new UInt64(id);
	}

	public static UInt64 of(String id) {
		return id.isEmpty() ? NONE : of(Long.parseUnsignedLong(id));
	}

	public static final Codec<UInt64> CODEC = Codec.STRING.comapFlatMap(id -> {
		if (!id.isEmpty()) {
			try {
				return DataResult.success(of(Long.parseUnsignedLong(id)));
			} catch (Exception ignored) {
			}
		}

		return DataResult.error(() -> "Invalid ID '" + id + "'");
	}, UInt64::toString);

	public static final StreamCodec<ByteBuf, UInt64> STREAM_CODEC = ByteBufCodecs.LONG.map(UInt64::of, UInt64::raw);
	public static final DataType<UInt64> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, UInt64.class);

	@Override
	public int hashCode() {
		return Long.hashCode(raw);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof UInt64 o && raw == o.raw;
	}

	@Override
	public @NotNull String toString() {
		return Long.toUnsignedString(raw);
	}
}
