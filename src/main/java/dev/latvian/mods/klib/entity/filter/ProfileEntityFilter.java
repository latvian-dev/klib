package dev.latvian.mods.klib.entity.filter;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record ProfileEntityFilter(GameProfile profile) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"profile",
		"profile",
		ExtraCodecs.AUTHLIB_GAME_PROFILE,
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
		return entity.getUUID().equals(profile.id()) || entity.getScoreboardName().equalsIgnoreCase(profile.name());
	}

	@Override
	@Nullable
	public Entity getFirst(Level level) {
		var entity = level.klib$getEntityByUUID(profile.id());

		if (entity != null) {
			return entity;
		}

		for (var e : level.klib$allEntities()) {
			if (e.getScoreboardName().equalsIgnoreCase(profile.name())) {
				return e;
			}
		}

		return null;
	}
}
