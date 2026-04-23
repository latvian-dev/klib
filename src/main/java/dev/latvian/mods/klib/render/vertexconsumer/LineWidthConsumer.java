package dev.latvian.mods.klib.render.vertexconsumer;

import com.mojang.blaze3d.vertex.VertexConsumer;

public record LineWidthConsumer(VertexConsumer delegate) implements DelegateVertexConsumer {
	@Override
	public VertexConsumer setLineWidth(float width) {
		delegate.setLineWidth(width);
		return this;
	}
}
