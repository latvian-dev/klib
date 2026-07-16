package dev.latvian.mods.klib.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public interface KLibBlockInWorld {
	static BlockInWorld of(LevelReader level, BlockPos pos, BlockState state) {
		var block = new BlockInWorld(level, pos, true);
		((KLibBlockInWorld) block).klib$setState(state);
		return block;
	}

	default void klib$setState(BlockState state) {
	}
}
