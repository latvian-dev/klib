package dev.latvian.mods.klib.util;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum GeometryRenderLayer implements StringRepresentable {
	NONE("none"),
	SOLID("solid"),
	CUTOUT_MIPPED("cutout_mipped"),
	CUTOUT("cutout"),
	TRANSLUCENT("translucent");

	public static final GeometryRenderLayer[] ALL = values();
	public static final Codec<GeometryRenderLayer> CODEC = KLibCodecs.anyEnum(ALL);
	public static final StreamCodec<ByteBuf, GeometryRenderLayer> STREAM_CODEC = KLibStreamCodecs.anyEnum(ALL);
	public static final DataType<GeometryRenderLayer> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC);

	private final String name;

	GeometryRenderLayer(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
