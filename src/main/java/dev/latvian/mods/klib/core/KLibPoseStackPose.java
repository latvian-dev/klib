package dev.latvian.mods.klib.core;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.klib.vertex.VertexCallback;

public interface KLibPoseStackPose {
	default void scale(float scale) {
		((PoseStack) this).scale(scale, scale, scale);
	}

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
