package dev.latvian.mods.klib.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.latvian.mods.klib.KLibMod;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import java.util.Optional;

@EventBusSubscriber(modid = KLibMod.ID, value = Dist.CLIENT)
public interface DebugRenderPipelines {
	RenderPipeline.Snippet BASE_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
		.withVertexShader("core/position_color")
		.withFragmentShader("core/position_color")
		.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
		.withCull(true)
		.buildSnippet();

	RenderPipeline LINES = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
		.withLocation(KLibMod.id("debug/lines"))
		.build();

	RenderPipeline LINES_SEE_THROUGH = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
		.withLocation(KLibMod.id("debug/lines_see_through"))
		.withDepthStencilState(Optional.empty())
		.build();

	RenderPipeline QUADS = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(KLibMod.id("debug/quads"))
		.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
		.build();

	RenderPipeline QUADS_NO_CULL = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(KLibMod.id("debug/quads_no_cull"))
		.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
		.withCull(false)
		.build();

	RenderPipeline QUADS_NO_DEPTH = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(KLibMod.id("debug/quads_no_depth"))
		.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
		.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
		.build();

	RenderPipeline QUADS_NO_CULL_NO_DEPTH = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(KLibMod.id("debug/quads_no_cull_no_depth"))
		.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
		.withCull(false)
		.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
		.build();

	RenderPipeline QUADS_SEE_THROUGH = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(KLibMod.id("debug/quads_see_through"))
		.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
		.withDepthStencilState(Optional.empty())
		.build();

	RenderPipeline QUADS_NO_CULL_SEE_THROUGH = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(KLibMod.id("debug/quads_no_cull_see_through"))
		.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
		.withCull(false)
		.withDepthStencilState(Optional.empty())
		.build();

	@SubscribeEvent
	static void registerRenderPipelines(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(LINES);
		event.registerPipeline(LINES_SEE_THROUGH);
		event.registerPipeline(QUADS);
		event.registerPipeline(QUADS_NO_CULL);
		event.registerPipeline(QUADS_NO_DEPTH);
		event.registerPipeline(QUADS_NO_CULL_NO_DEPTH);
		event.registerPipeline(QUADS_SEE_THROUGH);
		event.registerPipeline(QUADS_NO_CULL_SEE_THROUGH);
	}
}
