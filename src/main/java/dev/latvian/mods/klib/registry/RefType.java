package dev.latvian.mods.klib.registry;

import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum RefType {
	UNIT,
	DIRECT,
	CODE,
	SERVER,
	CLIENT;

	public static final RefType[] VALUES = values();
	public static final StreamCodec<ByteBuf, RefType> STREAM_CODEC = KLibStreamCodecs.anyEnum(VALUES);
}
