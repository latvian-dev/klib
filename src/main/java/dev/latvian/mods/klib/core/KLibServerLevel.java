package dev.latvian.mods.klib.core;

import dev.latvian.mods.klib.entity.EntityUtils;
import dev.latvian.mods.klib.util.BlockUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface KLibServerLevel extends KLibLevel {
	@Override
	default ServerLevel klib$self() {
		return (ServerLevel) this;
	}

	@Override
	default Iterable<Entity> klib$allEntities() {
		return klib$self().getEntities().getAll();
	}

	default int klib$discardAll(Predicate<Entity> filter) {
		return EntityUtils.discardAll(klib$self(), filter);
	}

	default int klib$discardAll(EntityType<?> type) {
		return EntityUtils.discardAll(klib$self(), type);
	}

	default int klib$killAll(Predicate<Entity> filter) {
		return EntityUtils.killAll(klib$self(), filter);
	}

	default int klib$killAll(EntityType<?> type) {
		return EntityUtils.killAll(klib$self(), type);
	}

	@Override
	default Stream<LevelChunk> klib$getChunks() {
		return BlockUtils.getServerLevelChunks(klib$self());
	}

	@Override
	default boolean klib$isLocalServer() {
		return klib$self().getServer().isSingleplayer();
	}
}
