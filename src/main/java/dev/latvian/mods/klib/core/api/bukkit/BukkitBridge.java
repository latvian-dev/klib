package dev.latvian.mods.klib.core.api.bukkit;

import net.minecraft.server.network.ConfigurationTask;

import java.util.List;
import java.util.function.Supplier;

public class BukkitBridge {
	public static List<Supplier<ConfigurationTask>> CONFIGURATION_TASKS = List.of();
}
