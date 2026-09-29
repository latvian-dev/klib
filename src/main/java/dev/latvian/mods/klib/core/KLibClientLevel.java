package dev.latvian.mods.klib.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.stream.Stream;

public interface KLibClientLevel extends KLibLevel {
	@Override
	default ClientLevel klib$self() {
		return (ClientLevel) this;
	}

	@Override
	default float klib$getDelta() {
		return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
	}

	@Override
	default Iterable<Entity> klib$allEntities() {
		return klib$self().entitiesForRendering();
	}

	@Override
	default Stream<LevelChunk> klib$getChunks() {
		var chunks = klib$self().getChunkSource().storage.chunks;
		int len = chunks.length();
		var list = new ArrayList<LevelChunk>(len);

		for (int i = 0; i < len; i++) {
			var chunk = chunks.get(i);

			if (chunk != null && !chunk.isEmpty()) {
				list.add(chunk);
			}
		}

		return list.stream();
	}

	@Override
	default void klib$setDayTime(long time) {
		klib$self().getLevelData().setDayTime(time);
	}

	@Override
	default boolean klib$isLocalServer() {
		var mc = Minecraft.getInstance();
		return mc.isLocalServer();
	}
}
