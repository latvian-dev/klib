package dev.latvian.mods.klib;

import dev.latvian.mods.klib.gradient.ClientGradientLoader;
import dev.latvian.mods.klib.util.ID;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

@Mod(value = KLib.ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = KLib.ID, value = Dist.CLIENT)
public class KLibClientMod {
	public KLibClientMod(ModContainer mod, IEventBus bus) {
	}

	@SubscribeEvent
	public static void addReloadListeners(AddClientReloadListenersEvent event) {
		event.addListener(ID.klib("gradient"), new ClientGradientLoader());
	}
}
