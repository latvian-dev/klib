package dev.latvian.mods.klib.block.collection;

import dev.latvian.mods.klib.block.BlockStatePalette;
import dev.latvian.mods.klib.block.PositionedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public record PositionedBlockStatePalette(List<BlockPos> positions, BlockStatePalette palette) implements BlockCollection {
	@Override
	public List<PositionedBlock> collectBlocks(RandomSource random) {
		var p = palette;
		var list = new ArrayList<PositionedBlock>(positions.size());

		for (var pos : positions) {
			list.add(new PositionedBlock(pos, p.sample(random)));
		}

		return list;
	}

	@Override
	public void forEach(BlockCollectionCallback callback, RandomSource random) {
		var p = palette;

		for (var pos : positions) {
			callback.accept(pos, p.sample(random));
		}
	}
}
