package dev.latvian.mods.klib.render;

import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.util.ID;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

@EventBusSubscriber(modid = KLib.ID, value = Dist.CLIENT)
public interface KLibRenderPipelines {
	RenderPipeline ENTITY_SOLID_NO_CULL = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
		.withLocation(ID.klib("pipeline/entity_solid_no_cull"))
		.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
		.withColorTargetState(ColorTargetState.DEFAULT)
		.withCull(false)
		.build();

	@SubscribeEvent
	static void registerRenderPipelines(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(ENTITY_SOLID_NO_CULL);
	}
}
