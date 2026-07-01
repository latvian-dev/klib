package dev.latvian.mods.klib.registry;

import dev.latvian.mods.klib.data.DataType;

@FunctionalInterface
public interface CustomRegistryCollector {
	<T> void register(DataType<T> dataType, CustomRegistry<?, T> registry);
}
