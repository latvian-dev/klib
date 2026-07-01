package dev.latvian.mods.klib.shape;

import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.util.ID;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3fc;

public enum EmptyShape implements Shape {
	INSTANCE;

	public static final CustomRegistryType<ByteBuf, Shape> TYPE = Shape.REGISTRY.unit(ID.klib("empty"), INSTANCE);

	@Override
	public CustomRegistryType<ByteBuf, Shape> type() {
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
