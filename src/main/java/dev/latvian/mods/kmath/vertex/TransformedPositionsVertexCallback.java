package dev.latvian.mods.kmath.vertex;

import org.joml.Matrix4fc;
import org.joml.Vector3f;

public record TransformedPositionsVertexCallback(VertexCallback delegate, Matrix4fc matrix, Vector3f temp) implements DelegatingVertexCallback {
	@Override
	public VertexCallback acceptPos(float x, float y, float z) {
		temp.set(x, y, z);
		temp.mulPosition(matrix);
		return delegate.acceptPos(temp.x, temp.y, temp.z);
	}
}
