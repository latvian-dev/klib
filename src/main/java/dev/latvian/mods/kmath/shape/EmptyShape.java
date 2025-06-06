package dev.latvian.mods.kmath.shape;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kmath.FrustumCheck;
import dev.latvian.mods.kmath.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3fc;

public enum EmptyShape implements Shape {
	INSTANCE;

	public static final MapCodec<EmptyShape> CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<ByteBuf, EmptyShape> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	public static final ShapeType TYPE = new ShapeType("empty", CODEC, STREAM_CODEC);

	@Override
	public ShapeType type() {
		return TYPE;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
	}

	@Override
	public boolean contains(Vector3fc p) {
		return false;
	}

	@Override
	public boolean isVisible(double x, double y, double z, FrustumCheck frustum) {
		return false;
	}

	@Override
	public String toString() {
		return "EmptyShape";
	}
}
