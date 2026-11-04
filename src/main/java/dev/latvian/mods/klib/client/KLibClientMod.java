package dev.latvian.mods.klib.client;

import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.platform.NeoClientPlatformHelper;
import dev.latvian.mods.klib.platform.PlatformHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = KLib.ID, dist = Dist.CLIENT)
public class KLibClientMod {
	public KLibClientMod(ModContainer mod, IEventBus bus) {
		PlatformHelper.INSTANCE = new NeoClientPlatformHelper(mod);
	}
}
