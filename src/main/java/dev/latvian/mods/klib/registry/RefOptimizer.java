package dev.latvian.mods.klib.registry;

public interface RefOptimizer<V> {
	default V optimize() {
		//noinspection unchecked
		return (V) this;
	}
}
