package dev.latvian.mods.klib.math;

import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import org.joml.Vector3fc;

public record Face(Vector3fc a, Vector3fc b, Vector3fc c, Vector3fc d, Vector3fc n) {
	public void forEachVertex(Vector3fc offset, QuadVertexCallback callback) {
		float ox = offset.x();
		float oy = offset.y();
		float oz = offset.z();

		callback.quadVertex(ox + a.x(), oy + a.y(), oz + a.z(), 0F, 0F, n.x(), n.y(), n.z());
		callback.quadVertex(ox + b.x(), oy + b.y(), oz + b.z(), 0F, 1F, n.x(), n.y(), n.z());
		callback.quadVertex(ox + c.x(), oy + c.y(), oz + c.z(), 1F, 1F, n.x(), n.y(), n.z());
		callback.quadVertex(ox + d.x(), oy + d.y(), oz + d.z(), 1F, 0F, n.x(), n.y(), n.z());
	}
}
