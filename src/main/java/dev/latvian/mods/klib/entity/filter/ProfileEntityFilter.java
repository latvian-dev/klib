package dev.latvian.mods.klib.entity.filter;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record ProfileEntityFilter(GameProfile profile) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"profile",
		"profile",
		ExtraCodecs.GAME_PROFILE,
		ByteBufCodecs.GAME_PROFILE,
		ProfileEntityFilter::new,
		ProfileEntityFilter::profile
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		return entity.getUUID().equals(profile.getId()) || (entity instanceof Player && entity.getScoreboardName().equalsIgnoreCase(profile.getName()));
	}

	@Override
	@Nullable
	public Entity getFirst(Level level) {
		var entity = level.getEntity(profile.getId());

		if (entity != null) {
			return entity;
		}

		for (var e : level.klib$allEntities()) {
			if (e instanceof Player && e.getScoreboardName().equalsIgnoreCase(profile.getName())) {
				return e;
			}
		}

		return null;
	}
}
