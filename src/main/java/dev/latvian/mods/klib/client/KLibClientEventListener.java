package dev.latvian.mods.klib.client;

import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.gradient.ClientGradientLoader;
import dev.latvian.mods.klib.icon.AtlasSpriteIcon;
import dev.latvian.mods.klib.icon.ItemIcon;
import dev.latvian.mods.klib.icon.SimpleColorIcon;
import dev.latvian.mods.klib.icon.TextureIcon;
import dev.latvian.mods.klib.icon.renderer.AtlasSpriteIconRenderState;
import dev.latvian.mods.klib.icon.renderer.IconRenderingRegistryEvent;
import dev.latvian.mods.klib.icon.renderer.ItemIconRenderState;
import dev.latvian.mods.klib.icon.renderer.SimpleColorIconRenderState;
import dev.latvian.mods.klib.icon.renderer.TextureIconRenderState;
import dev.latvian.mods.klib.util.ID;
import dev.latvian.mods.klib.visual.ShapeVisual;
import dev.latvian.mods.klib.visual.TexturedCubeVisual;
import dev.latvian.mods.klib.visual.renderer.ShapeVisualRenderState;
import dev.latvian.mods.klib.visual.renderer.TexturedCubeVisualRenderState;
import dev.latvian.mods.klib.visual.renderer.VisualRenderingRegistryEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = KLib.ID, value = Dist.CLIENT)
public class KLibClientEventListener {
	@SubscribeEvent
	public static void setup(FMLClientSetupEvent event) {
		ModLoader.postEvent(new IconRenderingRegistryEvent());
		ModLoader.postEvent(new VisualRenderingRegistryEvent());
	}

	@SubscribeEvent
	public static void addReloadListeners(AddClientReloadListenersEvent event) {
		event.addListener(ID.klib("gradient"), new ClientGradientLoader());
	}

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		KLibKeys.register(event);
	}

	@SubscribeEvent
	public static void registerIconRendering(IconRenderingRegistryEvent event) {
		event.register(SimpleColorIcon.TYPE, SimpleColorIconRenderState::new, SimpleColorIconRenderState::extract, SimpleColorIconRenderState::draw);
		event.register(TextureIcon.TYPE, TextureIconRenderState::new, TextureIconRenderState::extract, TextureIconRenderState::draw);
		event.register(ItemIcon.TYPE, ItemIconRenderState::new, ItemIconRenderState::extract, ItemIconRenderState::draw);
		event.register(AtlasSpriteIcon.TYPE, AtlasSpriteIconRenderState::new, AtlasSpriteIconRenderState::extract, AtlasSpriteIconRenderState::draw);
	}

	@SubscribeEvent
	public static void registerVisualRendering(VisualRenderingRegistryEvent event) {
		event.register(ShapeVisual.TYPE, ShapeVisualRenderState::new, ShapeVisualRenderState::extract);
		event.register(TexturedCubeVisual.TYPE, TexturedCubeVisualRenderState::new, TexturedCubeVisualRenderState::extract);
	}
}
