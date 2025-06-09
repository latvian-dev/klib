package dev.latvian.mods.klib.vertex;

import org.joml.Matrix3fc;
import org.joml.Vector3f;

public record TransformedNormalsVertexCallback(VertexCallback delegate, Matrix3fc matrix, boolean normalize, Vector3f temp) implements DelegatingVertexCallback {
	@Override
	public VertexCallback acceptNormal(float nx, float ny, float nz) {
		matrix.transform(nx, ny, nz, temp);

		if (normalize) {
			temp.normalize();
		}

		return delegate.acceptNormal(temp.x, temp.y, temp.z);
	}
}
