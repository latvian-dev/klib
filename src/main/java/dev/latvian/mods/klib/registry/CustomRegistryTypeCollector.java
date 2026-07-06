package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;

public interface CustomRegistryTypeCollector<B extends ByteBuf, T> {
	void register(CustomRegistryType<B, T> type);

	default void register(String id, T unit) {
		register(UnitType.create(id, unit));
	}
}
