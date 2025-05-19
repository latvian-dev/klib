package dev.latvian.mods.kmath.render.vertexconsumer;

import com.mojang.blaze3d.vertex.VertexConsumer;

public record PosColTexVertexConsumer(VertexConsumer delegate) implements DelegateVertexConsumer {
	@Override
	public VertexConsumer setColor(int red, int green, int blue, int alpha) {
		delegate.setColor(red, green, blue, alpha);
		return this;
	}

	@Override
	public VertexConsumer setUv(float u, float v) {
		delegate.setUv(u, v);
		return this;
	}
}
