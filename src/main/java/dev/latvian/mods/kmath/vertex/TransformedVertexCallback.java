package dev.latvian.mods.kmath.vertex;

import org.joml.Matrix3fc;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

public record TransformedVertexCallback(VertexCallback delegate, Matrix4fc posMatrix, Matrix3fc normalMatrix, boolean normalize, Vector3f temp) implements DelegatingVertexCallback {
	@Override
	public VertexCallback acceptPos(float x, float y, float z) {
		temp.set(x, y, z);
		temp.mulPosition(posMatrix);
		return delegate.acceptPos(temp.x, temp.y, temp.z);
	}

	@Override
	public VertexCallback acceptNormal(float nx, float ny, float nz) {
		normalMatrix.transform(nx, ny, nz, temp);

		if (normalize) {
			temp.normalize();
		}

		return delegate.acceptNormal(temp.x, temp.y, temp.z);
	}
}
