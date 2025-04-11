package dev.latvian.mods.kmath.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
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
		DefaultVertexFormat.POSITION_COLOR,
		VertexFormat.Mode.DEBUG_LINES,
		1536,
		RenderType.CompositeState.builder()
			.setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
			.setTextureState(new RenderStateShard.EmptyTextureStateShard(DebugRenderTypes::setupSmoothLines, DebugRenderTypes::clearSmoothLines))
			.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
			.setCullState(RenderStateShard.CULL)
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false)
	);

	RenderType QUADS = RenderType.create(
		"kmath:debug_quads",
		DefaultVertexFormat.POSITION_COLOR,
		VertexFormat.Mode.QUADS,
		1536,
		RenderType.CompositeState.builder()
			.setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
			.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
			.setCullState(RenderStateShard.CULL)
			.setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
			.setWriteMaskState(RenderStateShard.COLOR_WRITE)
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false)
	);

	RenderType QUADS_NO_CULL = RenderType.create(
		"kmath:debug_quads_no_cull",
		DefaultVertexFormat.POSITION_COLOR,
		VertexFormat.Mode.QUADS,
		1536,
		RenderType.CompositeState.builder()
			.setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
			.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
			.setCullState(RenderStateShard.NO_CULL)
			.setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
			.setWriteMaskState(RenderStateShard.COLOR_WRITE)
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false)
	);
}
