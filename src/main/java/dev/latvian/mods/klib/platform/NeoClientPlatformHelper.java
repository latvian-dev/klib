package dev.latvian.mods.klib.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.fml.ModContainer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.stream.Stream;

public class NeoClientPlatformHelper extends NeoPlatformHelper {
	public NeoClientPlatformHelper(ModContainer mod) {
		super(mod);
	}

	@Override
	public Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	@Override
	public Level getClientLevel() {
		return Minecraft.getInstance().level;
	}

	@Override
	public Iterable<Entity> getEntities(Level level) {
		if (level instanceof ClientLevel clientLevel) {
			return clientLevel.entitiesForRendering();
		} else {
			return super.getEntities(level);
		}
	}

	@Override
	public boolean isLocalServer(Level level) {
		if (level instanceof ClientLevel) {
			var mc = Minecraft.getInstance();
			return mc.isLocalServer();
		} else {
			return super.isLocalServer(level);
		}
	}

	@Override
	public Stream<LevelChunk> getLevelChunks(Level level) {
		if (level instanceof ClientLevel clientLevel) {
			var chunks = clientLevel.getChunkSource().storage.chunks;
			int len = chunks.length();
			var list = new ArrayList<LevelChunk>(len);

			for (int i = 0; i < len; i++) {
				var chunk = chunks.get(i);

				if (chunk != null && !chunk.isEmpty()) {
					list.add(chunk);
				}
			}

			return list.stream();
		} else {
			return super.getLevelChunks(level);
		}
	}

	@Override
	@Nullable
	public String getServerBrand(ReentrantBlockableEventLoop<?> environment) {
		if (environment instanceof Minecraft mc) {
			var p = mc.player;
			return p == null ? null : p.connection.serverBrand();
		} else {
			return super.getServerBrand(environment);
		}
	}
}
