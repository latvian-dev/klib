package dev.latvian.mods.klib.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.util.ID;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import java.util.Optional;

@EventBusSubscriber(modid = KLib.ID, value = Dist.CLIENT)
public interface DebugRenderPipelines {
	RenderPipeline.Snippet BASE_SNIPPET = RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
		.withVertexShader("core/position_color")
		.withFragmentShader("core/position_color")
		.withBindGroupLayout(BindGroupLayouts.PROJECTION)
		.withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
		.withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
		.withPrimitiveTopology(PrimitiveTopology.QUADS)
		.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
		.withCull(true)
		.buildSnippet();

	RenderPipeline LINES = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
		.withLocation(ID.klib("debug/lines"))
		.build();

	RenderPipeline LINES_SEE_THROUGH = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
		.withLocation(ID.klib("debug/lines_see_through"))
		.withDepthStencilState(Optional.empty())
		.build();

	RenderPipeline QUADS = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(ID.klib("debug/quads"))
		.build();

	RenderPipeline QUADS_NO_CULL = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(ID.klib("debug/quads_no_cull"))
		.withCull(false)
		.build();

	RenderPipeline QUADS_NO_DEPTH = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(ID.klib("debug/quads_no_depth"))
		.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
		.build();

	RenderPipeline QUADS_NO_CULL_NO_DEPTH = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(ID.klib("debug/quads_no_cull_no_depth"))
		.withCull(false)
		.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
		.build();

	RenderPipeline QUADS_SEE_THROUGH = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(ID.klib("debug/quads_see_through"))
		.withDepthStencilState(Optional.empty())
		.build();

	RenderPipeline QUADS_NO_CULL_SEE_THROUGH = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(ID.klib("debug/quads_no_cull_see_through"))
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
