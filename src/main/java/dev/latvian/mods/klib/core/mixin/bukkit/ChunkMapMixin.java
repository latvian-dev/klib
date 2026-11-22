package dev.latvian.mods.klib.core.mixin.bukkit;

import dev.latvian.mods.klib.core.api.KLibObject;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.stream.Stream;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin implements KLibObject {
	@Shadow
	abstract Stream<ChunkHolder> allChunksWithAtLeastStatus(ChunkStatus status);

	@Override
	public Object klib$get(int id) {
		if (id != 0) {
			return null;
		}

		var chunks = new Long2ObjectLinkedOpenHashMap<ChunkHolder>();
		allChunksWithAtLeastStatus(ChunkStatus.EMPTY).forEach(holder -> chunks.put(holder.getPos().pack(), holder));
		return chunks;
	}
}
