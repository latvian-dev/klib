package dev.latvian.mods.klib.math;

import dev.latvian.mods.klib.vertex.VertexCallback;
import org.joml.Vector3fc;

public record Face(Vector3fc a, Vector3fc b, Vector3fc c, Vector3fc d, Vector3fc n) {
	public void forEachVertex(VertexCallback callback) {
		callback.acceptPos(a.x(), a.y(), a.z()).acceptTex(0F, 0F).acceptNormal(n.x(), n.y(), n.z());
		callback.acceptPos(b.x(), b.y(), b.z()).acceptTex(0F, 1F).acceptNormal(n.x(), n.y(), n.z());
		callback.acceptPos(c.x(), c.y(), c.z()).acceptTex(1F, 1F).acceptNormal(n.x(), n.y(), n.z());
		callback.acceptPos(d.x(), d.y(), d.z()).acceptTex(1F, 0F).acceptNormal(n.x(), n.y(), n.z());
	}
}
