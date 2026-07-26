package dev.latvian.mods.klib.block.collection;

import dev.latvian.mods.klib.block.PositionedBlock;
import dev.latvian.mods.klib.registry.UnitType;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.RandomSource;

import java.util.List;

public enum EmptyBlockCollection implements BlockCollection {
	INSTANCE;

	@Override
	public UnitType<ByteBuf, BlockCollection> type() {
		return BlockCollection.EMPTY;
	}

	@Override
	public List<PositionedBlock> collectBlocks(RandomSource random) {
		return List.of();
	}

	@Override
	public void forEach(BlockCollectionCallback callback, RandomSource random) {
	}
}
