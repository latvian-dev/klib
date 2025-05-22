package dev.latvian.mods.kmath.core;

import dev.latvian.mods.kmath.vertex.VertexCallback;

public interface KMathPoseStackPose {
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
