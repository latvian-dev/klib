package dev.latvian.mods.klib.client;

import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.gradient.ClientGradientLoader;
import dev.latvian.mods.klib.icon.AtlasSpriteIcon;
import dev.latvian.mods.klib.icon.ItemIcon;
import dev.latvian.mods.klib.icon.SimpleColorIcon;
import dev.latvian.mods.klib.icon.TextureIcon;
import dev.latvian.mods.klib.icon.renderer.AtlasSpriteIconRenderState;
import dev.latvian.mods.klib.icon.renderer.IconRenderStateRegistryEvent;
import dev.latvian.mods.klib.icon.renderer.ItemIconRenderState;
import dev.latvian.mods.klib.icon.renderer.SimpleColorIconRenderState;
import dev.latvian.mods.klib.icon.renderer.TextureIconRenderState;
import dev.latvian.mods.klib.util.ID;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = KLib.ID, value = Dist.CLIENT)
public class KLibClientEventListener {
	@SubscribeEvent
	public static void addReloadListeners(AddClientReloadListenersEvent event) {
		event.addListener(ID.klib("gradient"), new ClientGradientLoader());
	}

	@SubscribeEvent
	public static void registerIconRenderStates(IconRenderStateRegistryEvent event) {
		event.register(SimpleColorIcon.class, SimpleColorIconRenderState::new);
		event.register(TextureIcon.class, TextureIconRenderState::new);
		event.register(ItemIcon.class, ItemIconRenderState::new);
		event.register(AtlasSpriteIcon.class, AtlasSpriteIconRenderState::new);
	}

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		KLibKeys.register(event);
	}
}
