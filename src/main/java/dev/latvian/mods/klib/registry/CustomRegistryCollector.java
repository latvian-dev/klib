package dev.latvian.mods.klib.registry;

@FunctionalInterface
public interface CustomRegistryCollector {
	<T> void register(CustomRegistry<?, T> registry);
}
