package dev.latvian.mods.klib.render.vertexconsumer;

import com.mojang.blaze3d.vertex.VertexConsumer;

public interface DelegateVertexConsumer extends VertexConsumer {
	VertexConsumer delegate();

	@Override
	default VertexConsumer addVertex(float x, float y, float z) {
		delegate().addVertex(x, y, z);
		return this;
	}

	@Override
	default VertexConsumer setColor(int red, int green, int blue, int alpha) {
		return this;
	}

	@Override
	default VertexConsumer setUv(float u, float v) {
		return this;
	}

	@Override
	default VertexConsumer setUv1(int u, int v) {
		return this;
	}

	@Override
	default VertexConsumer setUv2(int u, int v) {
		return this;
	}

	@Override
	default VertexConsumer setNormal(float nx, float ny, float nz) {
		return this;
	}
}
