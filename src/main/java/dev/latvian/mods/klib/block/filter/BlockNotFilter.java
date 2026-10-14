package dev.latvian.mods.klib.block.filter;

import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public record BlockNotFilter(Ref<BlockFilter> filter) implements BlockFilter {
	public static DynamicType<RegistryFriendlyByteBuf, BlockFilter> TYPE = DynamicType.create(
		"not",
		"filter",
		BlockFilter.CODEC,
		BlockFilter.STREAM_CODEC,
		BlockNotFilter::new,
		BlockNotFilter::filter
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, BlockFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(BlockInWorld block) {
		return !filter.value().test(block);
	}

	@Override
	public boolean test(LevelReader level, BlockPos pos, BlockState state) {
		return !filter.value().test(level, pos, state);
	}

	@Override
	public BlockFilter not() {
		return filter.value();
	}
}
