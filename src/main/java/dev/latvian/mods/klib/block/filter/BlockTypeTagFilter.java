package dev.latvian.mods.klib.block.filter;

import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public record BlockTypeTagFilter(TagKey<Block> tag) implements BlockFilter {
	public static final DynamicType<RegistryFriendlyByteBuf, BlockFilter> TYPE = DynamicType.create(
		"type_tag",
		"tag",
		TagKey.codec(Registries.BLOCK),
		TagKey.streamCodec(Registries.BLOCK),
		BlockTypeTagFilter::new,
		BlockTypeTagFilter::tag
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, BlockFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(BlockInWorld b) {
		return b.getState().is(tag);
	}

	@Override
	public boolean test(LevelReader level, BlockPos pos, BlockState state) {
		return state.is(tag);
	}
}
