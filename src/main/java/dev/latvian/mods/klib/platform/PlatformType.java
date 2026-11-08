package dev.latvian.mods.klib.platform;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum PlatformType implements StringRepresentable {
	OTHER("other"),
	NEOFORGE("neoforge"),
	BUKKIT("bukkit");

	public static final PlatformType[] VALUES = values();
	public static final Codec<PlatformType> CODEC = KLibCodecs.anyEnum(VALUES);
	public static final StreamCodec<ByteBuf, PlatformType> STREAM_CODEC = KLibStreamCodecs.anyEnum(VALUES);
	public static final DataType<PlatformType> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC);

	private final String name;

	PlatformType(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
