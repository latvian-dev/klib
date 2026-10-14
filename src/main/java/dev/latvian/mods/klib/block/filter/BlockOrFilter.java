package dev.latvian.mods.klib.block.filter;

import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.ArrayList;
import java.util.List;

public record BlockOrFilter(List<Ref<BlockFilter>> filters) implements BlockFilter {
	public static DynamicType<RegistryFriendlyByteBuf, BlockFilter> TYPE = DynamicType.create(
		"or",
		"filters",
		BlockFilter.CODEC.listOf(),
		KLibStreamCodecs.listOf(BlockFilter.STREAM_CODEC),
		BlockOrFilter::new,
		BlockOrFilter::filters
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, BlockFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(BlockInWorld block) {
		for (var filter : filters) {
			if (filter.value().test(block)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean test(LevelReader level, BlockPos pos, BlockState state) {
		for (var filter : filters) {
			if (filter.value().test(level, pos, state)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public BlockFilter or(BlockFilter filter) {
		if (filter == ANY.value()) {
			return filter;
		} else if (filter == NONE.value()) {
			return this;
		}

		var list = new ArrayList<Ref<BlockFilter>>(filters.size() + 1);
		list.addAll(filters);
		list.add(filter.ref());
		return new BlockOrFilter(List.copyOf(list));
	}
}
