package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.common.KLibChunkMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin implements KLibChunkMap {
	@Override
	@Accessor("visibleChunkMap")
	public abstract Long2ObjectLinkedOpenHashMap<ChunkHolder> klib$getVisibleChunkMap();
}
