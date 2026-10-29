package dev.latvian.mods.klib.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Comparator;
import java.util.function.Supplier;

public interface MiscUtils {
	Comparator<GameProfile> PROFILE_COMPARATOR = (a, b) -> a.name().compareToIgnoreCase(b.name());
	Comparator<NameAndId> NAME_AND_ID_COMPARATOR = (a, b) -> a.name().compareToIgnoreCase(b.name());

	RegistryAccess STATIC_REGISTRY_ACCESS = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

	Mutable<Supplier<Player>> CLIENT_PLAYER = new MutableObject<>(() -> null);

	static ClientAsset.ResourceTexture assetFromPNG(Identifier png) {
		return new ClientAsset.ResourceTexture(png.withPath(png.getPath().substring(9, png.getPath().length() - 4)));
	}
}
