package dev.latvian.mods.klib.block.filter;

import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public record BlockIdFilter(Block block) implements BlockFilter {
	public static final DynamicType<RegistryFriendlyByteBuf, BlockFilter> TYPE = DynamicType.create(
		"block",
		"block",
		BuiltInRegistries.BLOCK.byNameCodec(),
		KLibStreamCodecs.registry(BuiltInRegistries.BLOCK),
		BlockIdFilter::new,
		BlockIdFilter::block
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, BlockFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(BlockInWorld b) {
		return b.getState().is(block);
	}

	@Override
	public boolean test(LevelReader level, BlockPos pos, BlockState state) {
		return state.is(block);
	}
}
