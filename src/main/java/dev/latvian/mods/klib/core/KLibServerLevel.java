package dev.latvian.mods.klib.core;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface KLibServerLevel extends KLibLevel {
	@Override
	default ServerLevel klib$self() {
		return (ServerLevel) this;
	}

	@Override
	default Iterable<Entity> klib$allEntities() {
		return klib$self().getEntities().getAll();
	}

	@Override
	default void klib$discardAll(Predicate<Entity> filter) {
		var level = klib$self();

		for (var entity : level.getAllEntities()) {
			if (filter.test(entity)) {
				entity.discard();
			}
		}
	}

	@Override
	default void klib$killAll(Predicate<Entity> filter) {
		var level = klib$self();

		for (var entity : level.getAllEntities()) {
			if (filter.test(entity)) {
				entity.kill(level);
			}
		}
	}

	@Override
	default boolean klib$getTickDayTime() {
		return klib$self().getGameRules().getBoolean(GameRules.RULE_DAYLIGHT);
	}

	@Override
	default Stream<LevelChunk> klib$getChunks() {
		return StreamSupport.stream(klib$self().getChunkSource().chunkMap.getChunks().spliterator(), false).map(ChunkHolder::getTickingChunk).filter(c -> c != null && !c.isEmpty());
	}

	@Override
	default void klib$setDayTime(long time) {
		klib$self().setDayTime(time);
	}

	@Override
	default boolean klib$isLocalServer() {
		return klib$self().getServer().isSingleplayer();
	}
}
