package dev.latvian.mods.klib.render;

import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public interface DebugRenderTypes {
	static void setupThinLines() {
		// RenderSystem.lineWidth(Math.max(1.5F, Minecraft.getInstance().getWindow().getWidth() / 1920F * 1.5F));
	}

	static void clearThinLines() {
		// RenderSystem.lineWidth(1F);
	}

	RenderType LINES = RenderType.create(
		"klib:debug/lines",
		RenderSetup.builder(DebugRenderPipelines.LINES)
			// .withTexture("Sampler0", new RenderStateShard.EmptyTextureStateShard(DebugRenderTypes::setupThinLines, DebugRenderTypes::clearThinLines))
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.createRenderSetup()
	);

	RenderType LINES_SEE_THROUGH = RenderType.create(
		"klib:debug/lines_see_through",
		RenderSetup.builder(DebugRenderPipelines.LINES_SEE_THROUGH)
			// .setTextureState(new RenderStateShard.EmptyTextureStateShard(DebugRenderTypes::setupThinLines, DebugRenderTypes::clearThinLines))
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.createRenderSetup()
	);

	RenderType QUADS = RenderType.create(
		"klib:debug/quads",
		RenderSetup.builder(DebugRenderPipelines.QUADS)
			.createRenderSetup()
	);

	RenderType QUADS_NO_CULL = RenderType.create(
		"klib:debug/quads_no_cull",
		RenderSetup.builder(DebugRenderPipelines.QUADS_NO_CULL)
			.createRenderSetup()
	);

	RenderType QUADS_NO_DEPTH = RenderType.create(
		"klib:debug/quads_no_depth",
		RenderSetup.builder(DebugRenderPipelines.QUADS_NO_DEPTH)
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.createRenderSetup()
	);

	RenderType QUADS_NO_CULL_NO_DEPTH = RenderType.create(
		"klib:debug/quads_no_cull_no_depth",
		RenderSetup.builder(DebugRenderPipelines.QUADS_NO_CULL_NO_DEPTH)
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.createRenderSetup()
	);

	RenderType QUADS_SEE_THROUGH = RenderType.create(
		"klib:debug/quads_see_through",
		RenderSetup.builder(DebugRenderPipelines.QUADS_SEE_THROUGH)
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.createRenderSetup()
	);

	RenderType QUADS_NO_CULL_SEE_THROUGH = RenderType.create(
		"klib:debug/quads_no_cull_see_through",
		RenderSetup.builder(DebugRenderPipelines.QUADS_NO_CULL_SEE_THROUGH)
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.createRenderSetup()
	);
}
