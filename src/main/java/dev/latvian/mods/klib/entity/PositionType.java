package dev.latvian.mods.klib.entity;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum PositionType implements StringRepresentable {
	BOTTOM("bottom"),
	CENTER("center"),
	TOP("top"),
	EYES("eyes"),
	LEASH("leash"),
	SOUND_SOURCE("sound_source"),
	LOOK_TARGET("look_target"),

	;

	public static final PositionType[] VALUES = values();
	public static final DataType<PositionType> DATA_TYPE = DataType.of(VALUES);
	public static final Codec<PositionType> CODEC = DATA_TYPE.codec();
	public static final StreamCodec<ByteBuf, PositionType> STREAM_CODEC = KLibStreamCodecs.toBasic(DATA_TYPE.streamCodec());

	private final String name;

	PositionType(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
