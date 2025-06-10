package dev.latvian.mods.klib.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public interface DebugRenderTypes {
	static void setupThinLines() {
		RenderSystem.lineWidth(Math.max(1.5F, Minecraft.getInstance().getWindow().getWidth() / 1920F * 1.5F));
	}

	static void clearThinLines() {
		RenderSystem.lineWidth(1F);
	}

	RenderType LINES = RenderType.create(
		"klib:debug/lines",
		1536,
		DebugRenderPipelines.LINES,
		RenderType.CompositeState.builder()
			.setTextureState(new RenderStateShard.EmptyTextureStateShard(DebugRenderTypes::setupThinLines, DebugRenderTypes::clearThinLines))
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false)
	);

	RenderType LINES_SEE_THROUGH = RenderType.create(
		"klib:debug/lines_see_through",
		1536,
		DebugRenderPipelines.LINES_SEE_THROUGH,
		RenderType.CompositeState.builder()
			.setTextureState(new RenderStateShard.EmptyTextureStateShard(DebugRenderTypes::setupThinLines, DebugRenderTypes::clearThinLines))
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false)
	);

	RenderType QUADS = RenderType.create(
		"klib:debug/quads",
		1536,
		DebugRenderPipelines.QUADS,
		RenderType.CompositeState.builder()
			.createCompositeState(false)
	);

	RenderType QUADS_NO_CULL = RenderType.create(
		"klib:debug/quads_no_cull",
		1536,
		DebugRenderPipelines.QUADS_NO_CULL,
		RenderType.CompositeState.builder()
			.createCompositeState(false)
	);

	RenderType QUADS_NO_DEPTH = RenderType.create(
		"klib:debug/quads_no_depth",
		1536,
		DebugRenderPipelines.QUADS_NO_DEPTH,
		RenderType.CompositeState.builder()
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false)
	);

	RenderType QUADS_NO_CULL_NO_DEPTH = RenderType.create(
		"klib:debug/quads_no_cull_no_depth",
		1536,
		DebugRenderPipelines.QUADS_NO_CULL_NO_DEPTH,
		RenderType.CompositeState.builder()
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false)
	);

	RenderType QUADS_SEE_THROUGH = RenderType.create(
		"klib:debug/quads_see_through",
		1536,
		DebugRenderPipelines.QUADS_SEE_THROUGH,
		RenderType.CompositeState.builder()
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false)
	);

	RenderType QUADS_NO_CULL_SEE_THROUGH = RenderType.create(
		"klib:debug/quads_no_cull_see_through",
		1536,
		DebugRenderPipelines.QUADS_NO_CULL_SEE_THROUGH,
		RenderType.CompositeState.builder()
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false)
	);
}
