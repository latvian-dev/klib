package dev.latvian.mods.kmath.core;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.latvian.mods.kmath.vertex.VertexCallback;

public interface KMathVertexConsumer extends VertexCallback {
	@Override
	default VertexCallback acceptPos(float x, float y, float z) {
		((VertexConsumer) this).addVertex(x, y, z);
		return this;
	}

	@Override
	default VertexCallback acceptTex(float u, float v) {
		((VertexConsumer) this).setUv(u, v);
		return this;
	}

	@Override
	default VertexCallback acceptCol(float r, float g, float b, float a) {
		((VertexConsumer) this).setColor(r, g, b, a);
		return this;
	}

	@Override
	default VertexCallback acceptNormal(float nx, float ny, float nz) {
		((VertexConsumer) this).setNormal(nx, ny, nz);
		return this;
	}

	@Override
	default VertexCallback acceptLight(int u, int v) {
		((VertexConsumer) this).setUv2(u, v);
		return this;
	}

	@Override
	default VertexCallback acceptOverlay(int u, int v) {
		((VertexConsumer) this).setUv1(u, v);
		return this;
	}
}
