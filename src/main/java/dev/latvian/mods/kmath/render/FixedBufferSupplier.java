package dev.latvian.mods.kmath.render;

import dev.latvian.mods.kmath.vertex.VertexCallback;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public record FixedBufferSupplier(RenderType quadsCull, RenderType quadsNoCull, RenderType lines) implements BufferSupplier {
	@Override
	public VertexCallback quadsCull(MultiBufferSource buffers) {
		return buffers.getBuffer(quadsCull);
	}

	@Override
	public VertexCallback quadsNoCull(MultiBufferSource buffers) {
		return buffers.getBuffer(quadsNoCull);
	}

	@Override
	public VertexCallback lines(MultiBufferSource buffers) {
		return buffers.getBuffer(lines);
	}
}
