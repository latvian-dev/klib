package dev.latvian.mods.klib.render.vertexconsumer;

import com.mojang.blaze3d.vertex.VertexConsumer;

public record PosColVertexConsumer(VertexConsumer delegate) implements DelegateVertexConsumer {
	@Override
	public VertexConsumer setColor(int red, int green, int blue, int alpha) {
		delegate.setColor(red, green, blue, alpha);
		return this;
	}
}
