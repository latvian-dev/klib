package dev.latvian.mods.klib.block.filter;

import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public enum AnyBlockFilter implements BlockFilter {
	INSTANCE;

	@Override
	public UnitType<RegistryFriendlyByteBuf, BlockFilter> type() {
		return BlockFilter.ANY;
	}

	@Override
	public boolean test(BlockInWorld block) {
		return true;
	}

	@Override
	public boolean test(LevelReader level, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public BlockFilter and(BlockFilter filter) {
		return filter;
	}

	@Override
	public BlockFilter or(BlockFilter filter) {
		return this;
	}

	@Override
	public BlockFilter not() {
		return NoneBlockFilter.INSTANCE;
	}

	@Override
	public String toString() {
		return "any";
	}
}
