package dev.latvian.mods.klib.data;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import io.netty.buffer.ByteBuf;

public class DataTypeRegistryEvent extends CustomRegistryTypeEvent<ByteBuf, DataType<?>> {
	public DataTypeRegistryEvent(CustomRegistryTypeCollector<ByteBuf, DataType<?>> callback) {
		super(callback);
	}
}
