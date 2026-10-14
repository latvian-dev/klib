package dev.latvian.mods.klib.block.filter;

import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public enum NoneBlockFilter implements BlockFilter {
	INSTANCE;

	@Override
	public UnitType<RegistryFriendlyByteBuf, BlockFilter> type() {
		return BlockFilter.NONE;
	}

	@Override
	public boolean test(BlockInWorld block) {
		return false;
	}

	@Override
	public boolean test(LevelReader level, BlockPos pos, BlockState state) {
		return false;
	}

	@Override
	public BlockFilter and(BlockFilter filter) {
		return this;
	}

	@Override
	public BlockFilter or(BlockFilter filter) {
		return filter;
	}

	@Override
	public BlockFilter not() {
		return AnyBlockFilter.INSTANCE;
	}

	@Override
	public String toString() {
		return "none";
	}
}
