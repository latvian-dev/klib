package dev.latvian.mods.kmath.render;

import dev.latvian.mods.kmath.vertex.VertexCallback;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.function.UnaryOperator;

public interface BufferSupplier {
	BufferSupplier DEBUG = fixed(DebugRenderTypes.QUADS, DebugRenderTypes.QUADS_NO_CULL).process(VertexCallback::onlyPosCol);
	BufferSupplier DEBUG_NO_DEPTH = fixed(DebugRenderTypes.QUADS_NO_DEPTH, DebugRenderTypes.QUADS_NO_CULL_NO_DEPTH).process(VertexCallback::onlyPosCol);
	BufferSupplier DEBUG_SEE_THROUGH = fixed(DebugRenderTypes.QUADS_SEE_THROUGH, DebugRenderTypes.QUADS_NO_CULL_SEE_THROUGH, DebugRenderTypes.LINES_SEE_THROUGH).process(VertexCallback::onlyPosCol);

	static BufferSupplier fixed(RenderType quadsCull, RenderType quadsNoCull, RenderType lines) {
		return new FixedBufferSupplier(quadsCull, quadsNoCull, lines);
	}

	static BufferSupplier fixed(RenderType quadsCull, RenderType quadsNoCull) {
		return fixed(quadsCull, quadsNoCull, DebugRenderTypes.LINES);
	}

	static BufferSupplier fixed(RenderType quads) {
		return fixed(quads, quads, DebugRenderTypes.LINES);
	}

	VertexCallback quadsCull(MultiBufferSource buffers);

	VertexCallback quadsNoCull(MultiBufferSource buffers);

	default VertexCallback quads(MultiBufferSource buffers, boolean cull) {
		return cull ? quadsCull(buffers) : quadsNoCull(buffers);
	}

	default VertexCallback lines(MultiBufferSource buffers) {
		return buffers.getBuffer(DebugRenderTypes.LINES);
	}

	default BufferSupplier process(UnaryOperator<VertexCallback> process) {
		return new ProcessedBufferSupplier(this, process);
	}
}
