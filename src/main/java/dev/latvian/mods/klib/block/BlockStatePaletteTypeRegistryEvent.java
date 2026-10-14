package dev.latvian.mods.klib.block;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import io.netty.buffer.ByteBuf;

public class BlockStatePaletteTypeRegistryEvent extends CustomRegistryTypeEvent<ByteBuf, BlockStatePalette> {
	public BlockStatePaletteTypeRegistryEvent(CustomRegistryTypeCollector<ByteBuf, BlockStatePalette> callback) {
		super(callback);
	}
}