package dev.latvian.mods.kmath.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum ShapeType implements StringRepresentable {
	EMPTY("empty", EmptyShape.CODEC, EmptyShape.STREAM_CODEC),
	CUBE("cube", CubeShape.CODEC, CubeShape.STREAM_CODEC),
	CUBOID("cuboid", CuboidShape.CODEC, CuboidShape.STREAM_CODEC),
	SPHERE("sphere", SphereShape.CODEC, SphereShape.STREAM_CODEC),
	CYLINDER("cylinder", CylinderShape.CODEC, CylinderShape.STREAM_CODEC),

	;

	public static final ShapeType[] VALUES = values();
	public static final Codec<ShapeType> CODEC = StringRepresentable.fromEnum(() -> VALUES);
	public static final StreamCodec<ByteBuf, ShapeType> STREAM_CODEC = ByteBufCodecs.idMapper(i -> VALUES[i], Enum::ordinal);

	private final String name;
	private final MapCodec<? extends Shape> codec;
	private final StreamCodec<ByteBuf, ? extends Shape> streamCodec;

	ShapeType(String name, MapCodec<? extends Shape> codec, StreamCodec<ByteBuf, ? extends Shape> streamCodec) {
		this.name = name;
		this.codec = codec;
		this.streamCodec = streamCodec;
	}

	public MapCodec<? extends Shape> codec() {
		return codec;
	}

	public StreamCodec<ByteBuf, ? extends Shape> streamCodec() {
		return streamCodec;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
