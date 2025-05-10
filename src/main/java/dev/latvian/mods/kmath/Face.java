package dev.latvian.mods.kmath;

import org.joml.Vector3fc;

public record Face(Vector3fc a, Vector3fc b, Vector3fc c, Vector3fc d, Vector3fc n) {
	public void forEachVertex(VertexCallback.PosNormal callback) {
		callback.accept(a.x(), a.y(), a.z(), n.x(), n.y(), n.z());
		callback.accept(b.x(), b.y(), b.z(), n.x(), n.y(), n.z());
		callback.accept(c.x(), c.y(), c.z(), n.x(), n.y(), n.z());
		callback.accept(d.x(), d.y(), d.z(), n.x(), n.y(), n.z());
	}
}
