package dev.latvian.mods.klib.block.collection;

import dev.latvian.mods.klib.block.PositionedBlock;
import net.minecraft.util.RandomSource;

import java.util.List;

public record PositionedBlockCollection(List<PositionedBlock> blocks) implements BlockCollection {
	@Override
	public List<PositionedBlock> collectBlocks(RandomSource random) {
		return blocks;
	}

	@Override
	public void forEach(BlockCollectionCallback callback, RandomSource random) {
		for (var block : blocks) {
			block.forEach(callback, random);
		}
	}
}
