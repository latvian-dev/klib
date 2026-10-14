package dev.latvian.mods.klib.block.filter;

import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public abstract class SimpleBlockFilter implements BlockFilter {
	public static SimpleBlockFilter simple(UnitType<RegistryFriendlyByteBuf, BlockFilter> type, SimpleBlockPredicate predicate) {
		return new SimpleBlockFilter(type) {
			@Override
			public boolean test(BlockInWorld block) {
				var state = block.getState();
				return state != null && predicate.test(block.getLevel(), block.getPos(), state);
			}

			@Override
			public boolean test(LevelReader level, BlockPos pos, BlockState state) {
				return predicate.test(level, pos, state);
			}
		};
	}

	private final UnitType<RegistryFriendlyByteBuf, BlockFilter> type;

	public SimpleBlockFilter(UnitType<RegistryFriendlyByteBuf, BlockFilter> type) {
		this.type = type;
	}

	@Override
	public UnitType<RegistryFriendlyByteBuf, BlockFilter> type() {
		return type;
	}

	@Override
	public String toString() {
		return type.key();
	}
}
