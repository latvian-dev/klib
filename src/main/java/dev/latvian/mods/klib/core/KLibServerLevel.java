package dev.latvian.mods.klib.core;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

public interface KLibServerLevel extends KLibLevel {
	@Override
	@Nullable
	default Entity klib$getEntityByUUID(UUID uuid) {
		return ((ServerLevel) this).getEntity(uuid);
	}

	@Override
	default Iterable<Entity> klib$allEntities() {
		return ((ServerLevel) this).getEntities().getAll();
	}

	@Override
	default void klib$discardAll(Predicate<Entity> filter) {
		var level = (ServerLevel) this;

		for (var entity : level.getAllEntities()) {
			if (filter.test(entity)) {
				entity.discard();
			}
		}
	}

	@Override
	default void klib$killAll(Predicate<Entity> filter) {
		var level = (ServerLevel) this;

		for (var entity : level.getAllEntities()) {
			if (filter.test(entity)) {
				entity.kill(level);
			}
		}
	}

	@Override
	default boolean klib$getTickDayTime() {
		return ((ServerLevel) this).getGameRules().get(GameRules.ADVANCE_TIME);
	}
}
