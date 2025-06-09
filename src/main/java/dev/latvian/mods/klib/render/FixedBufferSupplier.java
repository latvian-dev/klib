package dev.latvian.mods.klib.render;

import dev.latvian.mods.klib.vertex.VertexCallback;
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
