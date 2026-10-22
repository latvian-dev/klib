package dev.latvian.mods.klib.math;

import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import org.joml.Vector3fc;

public record Face(Vector3fc a, Vector3fc b, Vector3fc c, Vector3fc d, Vector3fc n) {
	public void forEachVertex(QuadVertexCallback callback) {
		callback.quadVertex(a.x(), a.y(), a.z(), 0F, 0F, n.x(), n.y(), n.z());
		callback.quadVertex(b.x(), b.y(), b.z(), 0F, 1F, n.x(), n.y(), n.z());
		callback.quadVertex(c.x(), c.y(), c.z(), 1F, 1F, n.x(), n.y(), n.z());
		callback.quadVertex(d.x(), d.y(), d.z(), 1F, 0F, n.x(), n.y(), n.z());
	}
}
