package dev.latvian.mods.klib.registry;

import java.util.Collection;

@FunctionalInterface
public interface CustomRegistryUpdateCallback<V> {
	enum Type {
		REMOTE,
		CLIENT,
		SERVER
	}

	void onUpdate(Collection<Ref<V>> values, Type type);
}
