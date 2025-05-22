package dev.latvian.mods.kmath.core;

import dev.latvian.mods.kmath.vertex.VertexCallback;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public interface KMathPoseStackPose {
	Matrix3fc IDENTITY_3x3 = new Matrix3f();
	Matrix4fc IDENTITY_4x4 = new Matrix4f();

	default VertexCallback transform(VertexCallback callback) {
		return transformNormals(transformPositions(callback));
	}

	default VertexCallback transformPositions(VertexCallback callback) {
		return callback;
	}

	default VertexCallback transformNormals(VertexCallback callback) {
		return callback;
	}
}
