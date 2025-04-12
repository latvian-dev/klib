package dev.latvian.mods.kmath.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.latvian.mods.kmath.KMathMod;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

@EventBusSubscriber(modid = KMathMod.ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public interface DebugRenderPipelines {
	RenderPipeline.Snippet BASE_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_SNIPPET)
		.withVertexShader("core/position_color")
		.withFragmentShader("core/position_color")
		.withBlend(BlendFunction.TRANSLUCENT)
		.withCull(true)
		.buildSnippet();

	RenderPipeline LINES = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(KMathMod.id("debug/lines"))
		.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINES)
		.build();

	RenderPipeline QUADS = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(KMathMod.id("debug/quads"))
		.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
		.withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
		.withDepthWrite(false)
		.build();

	RenderPipeline QUADS_NO_CULL = RenderPipeline.builder(BASE_SNIPPET)
		.withLocation(KMathMod.id("debug/quads_no_cull"))
		.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
		.withCull(false)
		.withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
		.withDepthWrite(false)
		.build();

	@SubscribeEvent
	static void registerRenderPipelines(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(LINES);
		event.registerPipeline(QUADS);
		event.registerPipeline(QUADS_NO_CULL);
	}
}
