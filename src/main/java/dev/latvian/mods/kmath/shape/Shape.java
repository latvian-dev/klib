package dev.latvian.mods.kmath.shape;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kmath.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface Shape {
	Codec<Shape> DIRECT_CODEC = ShapeType.CODEC.dispatch("type", Shape::type, ShapeType::codec);
	Codec<Shape> UNIT_OR_DIRECT_CODEC = Codec.either(UnitShape.CODEC, DIRECT_CODEC).xmap(e -> e.map(Function.identity(), Function.identity()), s -> s instanceof UnitShape u ? Either.left(u) : Either.right(s));
	Codec<Shape> CODEC = UNIT_OR_DIRECT_CODEC.xmap(Shape::optimize, Function.identity());
	StreamCodec<ByteBuf, Shape> DIRECT_STREAM_CODEC = ShapeType.STREAM_CODEC.dispatch(Shape::type, ShapeType::streamCodec);
	StreamCodec<ByteBuf, Shape> STREAM_CODEC = ByteBufCodecs.either(UnitShape.STREAM_CODEC, DIRECT_STREAM_CODEC).map(e -> e.map(Function.identity(), Function.identity()), s -> s instanceof UnitShape u ? Either.left(u) : Either.right(s));

	ShapeType type();

	default Shape optimize() {
		return this;
	}

	void buildLines(float x, float y, float z, VertexCallback callback);

	void buildQuads(float x, float y, float z, VertexCallback callback);
}
