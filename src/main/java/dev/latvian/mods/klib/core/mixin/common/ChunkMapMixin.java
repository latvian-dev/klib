package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.KLibObject;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin implements KLibObject {
	@Shadow
	private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap;

	@Override
	public Object klib$get(int id) {
		return id == 0 ? visibleChunkMap : null;
	}
}
