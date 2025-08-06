package dev.latvian.mods.klib;

import dev.latvian.mods.klib.math.ClientMatrices;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.FrameGraphSetupEvent;

@EventBusSubscriber(modid = KLibMod.ID, value = Dist.CLIENT)
public class KLibClientEventHandler {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void setup(FrameGraphSetupEvent event) {
		ClientMatrices.updateMain(event.getModelViewMatrix(), event.getProjectionMatrix());
	}
}
