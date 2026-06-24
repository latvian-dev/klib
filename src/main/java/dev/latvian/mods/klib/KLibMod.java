package dev.latvian.mods.klib;

import dev.latvian.mods.klib.command.KLibCommandArgumentTypes;
import dev.latvian.mods.klib.data.DataTypeRegistry;
import dev.latvian.mods.klib.data.DataTypeRegistryEvent;
import dev.latvian.mods.klib.platform.NeoPlatformHelper;
import dev.latvian.mods.klib.platform.PlatformHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(KLib.ID)
@EventBusSubscriber(modid = KLib.ID)
public class KLibMod {
	public KLibMod(ModContainer mod, IEventBus bus) {
		PlatformHelper.CURRENT = new NeoPlatformHelper(mod);
		KLib.VERSION = mod.getModInfo().getVersion().toString();
		KLibCommandArgumentTypes.REGISTRY.register(bus);
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event) {
		DataTypeRegistry.registerAll(registry -> ModLoader.postEvent(new DataTypeRegistryEvent(registry)));
	}

	@SubscribeEvent
	public static void dataTypeRegistry(DataTypeRegistryEvent event) {
		KLib.registerDataTypes(event.getRegistry());
	}
}
