package dev.latvian.mods.klib.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;

import java.util.Comparator;

public interface MiscUtils {
	Comparator<GameProfile> PROFILE_COMPARATOR = (a, b) -> a.name().compareToIgnoreCase(b.name());
	Comparator<NameAndId> NAME_AND_ID_COMPARATOR = (a, b) -> a.name().compareToIgnoreCase(b.name());

	RegistryAccess STATIC_REGISTRY_ACCESS = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

	static void runServerCommand(MinecraftServer server, String command) {
		server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
	}
}
