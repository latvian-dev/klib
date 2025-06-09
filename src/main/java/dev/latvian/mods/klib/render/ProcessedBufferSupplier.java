package dev.latvian.mods.klib.render;

import dev.latvian.mods.klib.vertex.VertexCallback;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.function.UnaryOperator;

public record ProcessedBufferSupplier(BufferSupplier delegate, UnaryOperator<VertexCallback> process) implements BufferSupplier {
	@Override
	public VertexCallback quadsCull(MultiBufferSource buffers) {
		return process.apply(delegate.quadsCull(buffers));
	}

	@Override
	public VertexCallback quadsNoCull(MultiBufferSource buffers) {
		return process.apply(delegate.quadsNoCull(buffers));
	}

	@Override
	public VertexCallback lines(MultiBufferSource buffers) {
		return delegate.lines(buffers);
	}
}
