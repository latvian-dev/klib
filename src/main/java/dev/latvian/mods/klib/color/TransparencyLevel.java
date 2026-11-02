package dev.latvian.mods.klib.color;

import com.mojang.blaze3d.platform.Transparency;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum TransparencyLevel {
	SOLID,
	TRANSPARENT,
	TRANSLUCENT;

	public static final TransparencyLevel[] VALUES = values();
	public static final Codec<TransparencyLevel> CODEC = KLibCodecs.anyEnum(VALUES);
	public static final StreamCodec<ByteBuf, TransparencyLevel> STREAM_CODEC = KLibStreamCodecs.anyEnum(VALUES);
	public static final DataType<TransparencyLevel> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC);

	public static TransparencyLevel from(Transparency transparency) {
		if (transparency.hasTranslucent()) {
			return TRANSLUCENT;
		} else if (transparency.hasTransparent()) {
			return TRANSPARENT;
		} else {
			return SOLID;
		}
	}

	public TransparencyLevel min(TransparencyLevel other) {
		return VALUES[Math.min(ordinal(), other.ordinal())];
	}

	public TransparencyLevel max(TransparencyLevel other) {
		return VALUES[Math.max(ordinal(), other.ordinal())];
	}
}
