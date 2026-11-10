package dev.latvian.mods.klib.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.GpuFormat;
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
public interface KLibRenderPipelines {
	RenderPipeline ENTITY_SOLID_NO_CULL = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
		.withLocation(ID.klib("pipeline/entity_solid_no_cull"))
		.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
		.withColorTargetState(ColorTargetState.DEFAULT)
		.withCull(false)
		.build();

	RenderPipeline ADDITIVE_PARTICLE = RenderPipeline.builder()
		.withLocation(ID.klib("pipeline/particle/additive"))
		.withBindGroupLayout(BindGroupLayouts.GLOBALS)
		.withBindGroupLayout(BindGroupLayouts.PROJECTION)
		.withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
		.withBindGroupLayout(BindGroupLayouts.FOG)
		.withBindGroupLayout(BindGroupLayouts.SAMPLER0)
		.withBindGroupLayout(BindGroupLayouts.SAMPLER2)
		.withColorTargetState(new ColorTargetState(BlendFunction.ADDITIVE))
		.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
		.withVertexShader("core/particle")
		.withFragmentShader("core/particle")
		.withVertexBinding(0, DefaultVertexFormat.PARTICLE)
		.withPrimitiveTopology(PrimitiveTopology.QUADS)
		.build();

	RenderPipeline ADDITIVE_PARTICLE_ONLY_DEPTH = RenderPipeline.builder()
		.withLocation(ID.klib("pipeline/particle/additive_only_depth"))
		.withBindGroupLayout(BindGroupLayouts.GLOBALS)
		.withBindGroupLayout(BindGroupLayouts.PROJECTION)
		.withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
		.withBindGroupLayout(BindGroupLayouts.FOG)
		.withBindGroupLayout(BindGroupLayouts.SAMPLER0)
		.withBindGroupLayout(BindGroupLayouts.SAMPLER2)
		.withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.ADDITIVE), GpuFormat.RGBA8_UNORM, ColorTargetState.WRITE_NONE))
		.withDepthStencilState(DepthStencilState.DEFAULT)
		.withVertexShader("core/particle")
		.withFragmentShader("core/particle")
		.withVertexBinding(0, DefaultVertexFormat.PARTICLE)
		.withPrimitiveTopology(PrimitiveTopology.QUADS)
		.build();

	@SubscribeEvent
	static void registerRenderPipelines(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(ENTITY_SOLID_NO_CULL);
		event.registerPipeline(ADDITIVE_PARTICLE);
		event.registerPipeline(ADDITIVE_PARTICLE_ONLY_DEPTH);
	}
}
