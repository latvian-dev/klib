package dev.latvian.mods.kmath.render;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

public interface DebugRenderTypes {
	static void setupSmoothLines() {
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
	}

	static void clearSmoothLines() {
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

	RenderType LINES = RenderType.create(
		"kmath:debug_lines",
		1536,
		DebugRenderPipelines.LINES,
		RenderType.CompositeState.builder()
			.setTextureState(new RenderStateShard.EmptyTextureStateShard(DebugRenderTypes::setupSmoothLines, DebugRenderTypes::clearSmoothLines))
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false)
	);

	RenderType QUADS = RenderType.create(
		"kmath:debug_quads",
		1536,
		DebugRenderPipelines.QUADS,
		RenderType.CompositeState.builder()
			.createCompositeState(false)
	);

	RenderType QUADS_NO_CULL = RenderType.create(
		"kmath:debug_quads_no_cull",
		1536,
		DebugRenderPipelines.QUADS_NO_CULL,
		RenderType.CompositeState.builder()
			.createCompositeState(false)
	);

	RenderType QUADS_NO_DEPTH = RenderType.create(
		"kmath:debug_quads_no_depth",
		1536,
		DebugRenderPipelines.QUADS_NO_DEPTH,
		RenderType.CompositeState.builder()
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false)
	);

	RenderType QUADS_NO_CULL_NO_DEPTH = RenderType.create(
		"kmath:debug_quads_no_cull_no_depth",
		1536,
		DebugRenderPipelines.QUADS_NO_CULL_NO_DEPTH,
		RenderType.CompositeState.builder()
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false)
	);
}
