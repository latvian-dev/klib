package dev.latvian.mods.klib.util;

import dev.latvian.mods.klib.core.KLibExtensions;
import dev.latvian.mods.klib.platform.PlatformHelper;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.stream.Stream;

public interface GameUtils {
	static MinecraftServer getServer(ServerCommonPacketListenerImpl packetListener) {
		return KLibExtensions.get(packetListener, 0);
	}

	static Stream<LevelChunk> getChunks(Level level) {
		return PlatformHelper.INSTANCE.getLevelChunks(level);
	}

	static Stream<BlockEntity> getAllBlockEntities(Level level) {
		return getChunks(level).flatMap(c -> c.getBlockEntities().values().stream());
	}

	static Long2ObjectLinkedOpenHashMap<ChunkHolder> getVisibleChunkMap(ChunkMap chunkMap) {
		return KLibExtensions.get(chunkMap, 0);
	}
}
