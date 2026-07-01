package dev.latvian.mods.klib.shape;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import io.netty.buffer.ByteBuf;

public class ShapeTypeRegistryEvent extends CustomRegistryTypeEvent<ByteBuf, Shape> {
	public ShapeTypeRegistryEvent(CustomRegistryTypeCollector<ByteBuf, Shape> callback) {
		super(callback);
	}
}
