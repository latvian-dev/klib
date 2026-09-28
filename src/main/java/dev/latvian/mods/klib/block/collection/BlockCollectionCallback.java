package dev.latvian.mods.klib.block.collection;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BlockCollectionCallback {
	void accept(BlockPos pos, BlockState state);
}
