package dev.latvian.mods.klib.shape;

import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3fc;

public enum EmptyShape implements Shape {
	INSTANCE;

	@Override
	public UnitType<ByteBuf, Shape> type() {
		return EMPTY;
	}

	@Override
	public void buildLines(LineVertexCallback callback) {
	}

	@Override
	public void buildQuads(QuadVertexCallback callback) {
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
