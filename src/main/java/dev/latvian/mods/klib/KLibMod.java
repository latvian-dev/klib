package dev.latvian.mods.klib;

import dev.latvian.mods.klib.data.DataTypes;
import dev.latvian.mods.klib.data.JOMLDataTypes;
import dev.latvian.mods.klib.platform.NeoPlatformHelper;
import dev.latvian.mods.klib.platform.PlatformHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(KLib.ID)
@EventBusSubscriber(modid = KLib.ID, value = Dist.CLIENT)
public class KLibMod {
	public KLibMod(ModContainer mod, IEventBus bus) {
		PlatformHelper.CURRENT = new NeoPlatformHelper(mod);
		KLib.VERSION = mod.getModInfo().getVersion().toString();
		DataTypes.register();
		JOMLDataTypes.register();
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event) {
	}
}
