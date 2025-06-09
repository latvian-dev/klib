package dev.latvian.mods.klib.core;

import dev.latvian.mods.klib.vertex.VertexCallback;

public interface KLibPoseStackPose {
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
