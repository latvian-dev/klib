package dev.latvian.mods.klib.block.filter;

import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public record BlockStateFilter(BlockState blockState) implements BlockFilter {
	public static final DynamicType<RegistryFriendlyByteBuf, BlockFilter> TYPE = DynamicType.create(
		"block_state",
		"block_state",
		MCCodecs.BLOCK_STATE,
		MCStreamCodecs.BLOCK_STATE,
		BlockStateFilter::new,
		BlockStateFilter::blockState
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, BlockFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(BlockInWorld block) {
		return block.getState() == blockState;
	}

	@Override
	public boolean test(LevelReader level, BlockPos pos, BlockState state) {
		return state == blockState;
	}
}
