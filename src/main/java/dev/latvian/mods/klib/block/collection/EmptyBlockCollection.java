package dev.latvian.mods.klib.block.collection;

import dev.latvian.mods.klib.registry.UnitType;
import io.netty.buffer.ByteBuf;

public enum EmptyBlockCollection implements BlockCollection {
	INSTANCE;

	@Override
	public UnitType<ByteBuf, BlockCollection> type() {
		return BlockCollection.EMPTY;
	}
}
