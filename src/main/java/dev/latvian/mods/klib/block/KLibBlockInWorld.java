package dev.latvian.mods.klib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class KLibBlockInWorld extends BlockInWorld {
	public static KLibBlockInWorld of(LevelReader level, BlockPos pos, BlockState state) {
		return new KLibBlockInWorld(level, pos, state);
	}

	public final BlockState state;

	public KLibBlockInWorld(LevelReader level, BlockPos pos, BlockState state) {
		super(level, pos, false);
		this.state = state;
	}

	@Override
	public BlockState getState() {
		return state;
	}
}
