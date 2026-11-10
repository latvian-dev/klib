package dev.latvian.mods.klib.particle;

import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.render.KLibRenderPipelines;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleGroupsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = KLib.ID, value = Dist.CLIENT)
public interface KLibClientParticles {
	ParticleRenderType CUSTOM_RENDER_TYPE = new ParticleRenderType("KLIB_CUSTOM", "KL_C");

	SingleQuadParticle.Layer ADDITIVE_LAYER = new SingleQuadParticle.Layer(
		true,
		TextureAtlas.LOCATION_PARTICLES,
		KLibRenderPipelines.ADDITIVE_PARTICLE,
		RenderPipelines.OIT_PARTICLE
	);

	SingleQuadParticle.Layer ADDITIVE_ONLY_DEPTH_LAYER = new SingleQuadParticle.Layer(
		true,
		TextureAtlas.LOCATION_PARTICLES,
		KLibRenderPipelines.ADDITIVE_PARTICLE_ONLY_DEPTH,
		RenderPipelines.OIT_PARTICLE
	);

	@SubscribeEvent
	static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpecial(KLibParticles.SHAPE.get(), ShapeParticle::new);
		// event.registerSpecial(KLibParticles.LINE.get(), LineParticle::new);
		// event.registerSpecial(KLibParticles.TEXT.get(), TextParticle::new);
		// event.registerSpecial(KLibParticles.ITEM.get(), ItemParticle::new);
	}

	@SubscribeEvent
	static void registerParticleGroups(RegisterParticleGroupsEvent event) {
		event.register(CUSTOM_RENDER_TYPE, CustomParticleGroup::new);
	}
}
