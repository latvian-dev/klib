package dev.latvian.mods.klib.block.collection;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import io.netty.buffer.ByteBuf;

public class BlockCollectionTypeRegistryEvent extends CustomRegistryTypeEvent<ByteBuf, BlockCollection> {
	public BlockCollectionTypeRegistryEvent(CustomRegistryTypeCollector<ByteBuf, BlockCollection> callback) {
		super(callback);
	}
}