package dev.latvian.mods.klib.shape;

import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3fc;

public enum UniverseShape implements Shape {
	INSTANCE;

	@Override
	public UnitType<ByteBuf, Shape> type() {
		return UNIVERSE;
	}

	@Override
	public void buildLines(LineVertexCallback callback) {
	}

	@Override
	public void buildQuads(QuadVertexCallback callback) {
	}

	@Override
	public boolean contains(Vector3fc p) {
		return true;
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
