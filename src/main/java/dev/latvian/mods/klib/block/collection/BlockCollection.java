package dev.latvian.mods.klib.block.collection;

import dev.latvian.mods.klib.block.PositionedBlock;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public interface BlockCollection {
	default List<PositionedBlock> collectBlocks(RandomSource random) {
		var list = new ArrayList<PositionedBlock>(1);
		forEach((pos, state) -> list.add(new PositionedBlock(pos, state)), random);
		return list;
	}

	void forEach(BlockCollectionCallback callback, RandomSource random);
}
