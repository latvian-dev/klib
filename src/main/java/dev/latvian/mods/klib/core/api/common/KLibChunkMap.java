package dev.latvian.mods.klib.core.api.common;

import dev.latvian.mods.klib.core.NoMixinException;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.level.ChunkHolder;

public interface KLibChunkMap {
	default Long2ObjectLinkedOpenHashMap<ChunkHolder> klib$getVisibleChunkMap() {
		throw new NoMixinException(this);
	}
}
