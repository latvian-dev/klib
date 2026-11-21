package dev.latvian.mods.klib.core.api.bukkit;

import dev.latvian.mods.klib.KLibBuiltInRegistries;

public class BukkitIntegration {
	public static void init() {
		try {
			var bridge = Class.forName(
				"dev.latvian.mods.klib.core.api.bukkit.BukkitBridge",
				true,
				BukkitIntegration.class.getClassLoader().getParent()
			);

			bridge.getDeclaredField("configurationTasks").set(null, KLibBuiltInRegistries.CONFIGURATION_TASKS);
		} catch (Throwable ex) {
			throw new RuntimeException("Couldn't load KLib's Bukkit integration", ex);
		}
	}
}
