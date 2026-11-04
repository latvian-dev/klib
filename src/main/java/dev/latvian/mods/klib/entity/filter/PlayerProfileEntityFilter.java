package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record PlayerProfileEntityFilter(NameAndId profile) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"player_profile",
		"profile",
		NameAndId.CODEC,
		MCStreamCodecs.NAME_AND_ID,
		PlayerProfileEntityFilter::new,
		PlayerProfileEntityFilter::profile
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		return entity instanceof Player player && (player.getUUID().equals(profile.id()) || player.getGameProfile().name().equalsIgnoreCase(profile.name()));
	}

	@Override
	@Nullable
	public Player getFirst(Level level) {
		var entity = level.getEntity(profile.id());

		if (entity != null) {
			return entity instanceof Player player ? player : null;
		}

		for (var player : level.players()) {
			if (player.getGameProfile().name().equalsIgnoreCase(profile.name())) {
				return player;
			}
		}

		return null;
	}
}
