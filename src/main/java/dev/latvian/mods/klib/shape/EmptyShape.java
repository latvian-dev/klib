package dev.latvian.mods.klib.shape;

import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3fc;

public enum EmptyShape implements Shape {
	INSTANCE;

	public static final UnitType<ByteBuf, Shape> TYPE = UnitType.create("empty", INSTANCE);

	@Override
	public UnitType<ByteBuf, Shape> type() {
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
