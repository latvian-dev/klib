package dev.latvian.mods.kmath.shape;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kmath.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum EmptyShape implements Shape {
	INSTANCE;

	public static final MapCodec<EmptyShape> CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<ByteBuf, EmptyShape> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public ShapeType type() {
		return ShapeType.EMPTY;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
	}
}
