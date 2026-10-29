package dev.latvian.mods.klib.client;

import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = KLib.ID, dist = Dist.CLIENT)
public class KLibClientMod {
	public KLibClientMod(ModContainer mod, IEventBus bus) {
		MiscUtils.CLIENT_PLAYER.setValue(() -> Minecraft.getInstance().player);
	}
}
