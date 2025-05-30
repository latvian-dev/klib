package dev.latvian.mods.kmath.shape;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kmath.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface Shape {
	Codec<Shape> DIRECT_CODEC = ShapeType.CODEC.dispatch("type", Shape::type, ShapeType::codec);
	Codec<Shape> CODEC = DIRECT_CODEC.xmap(Shape::optimize, Function.identity());
	StreamCodec<ByteBuf, Shape> STREAM_CODEC = ShapeType.STREAM_CODEC.dispatch(Shape::type, ShapeType::streamCodec);

	ShapeType type();

	default Shape optimize() {
		return this;
	}

	void buildLines(float x, float y, float z, VertexCallback callback);

	void buildQuads(float x, float y, float z, VertexCallback callback);
}
