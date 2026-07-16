package dev.latvian.mods.klib.block.filter;

import dev.latvian.mods.klib.core.KLibBlockInWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.function.Predicate;

@FunctionalInterface
public interface BlockPredicate extends Predicate<BlockInWorld>, SimpleBlockPredicate {
	static BlockPredicate of(SimpleBlockPredicate simple) {
		return new BlockPredicate() {
			@Override
			public boolean test(BlockInWorld block) {
				return simple.test(block.getLevel(), block.getPos(), block.getState());
			}

			@Override
			public boolean test(LevelReader level, BlockPos pos, BlockState state) {
				return simple.test(level, pos, state);
			}
		};
	}

	@Override
	default boolean test(LevelReader level, BlockPos pos, BlockState state) {
		return test(KLibBlockInWorld.of(level, pos, state));
	}
}
