package dev.latvian.mods.klib.shape;

import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;

public enum EmptyShape implements Shape {
	INSTANCE;

	@Override
	public UnitType<ByteBuf, Shape> type() {
		return EMPTY;
	}

	@Override
	public AAFBB box() {
		return AAFBB.EMPTY;
	}

	@Override
	public void buildLines(LineVertexCallback callback) {
	}

	@Override
	public void buildQuads(QuadVertexCallback callback) {
	}

	@Override
	public boolean contains(float x, float y, float z) {
		return false;
	}

	@Override
	public float intersects(float x0, float y0, float z0, float x1, float y1, float z1) {
		return Float.NaN;
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
