package dev.latvian.mods.klib.block.filter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface SimpleBlockPredicate {
	boolean test(LevelReader level, BlockPos pos, BlockState state);
}
