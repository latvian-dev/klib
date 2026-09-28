package dev.latvian.mods.klib.block.collection;

import dev.latvian.mods.klib.block.PositionedBlock;
import net.minecraft.util.RandomSource;

import java.util.List;

public enum EmptyBlockCollection implements BlockCollection {
	INSTANCE;

	@Override
	public List<PositionedBlock> collectBlocks(RandomSource random) {
		return List.of();
	}

	@Override
	public void forEach(BlockCollectionCallback callback, RandomSource random) {
	}
}
