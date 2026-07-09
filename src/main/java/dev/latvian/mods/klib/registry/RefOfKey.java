package dev.latvian.mods.klib.registry;

import org.jetbrains.annotations.Nullable;

public final class RefOfKey<V> implements Ref<V> {
	private final String key;
	V value;

	RefOfKey(String key) {
		this.key = key;
		this.value = null;
	}

	@Override
	public String optionalKey() {
		return key;
	}

	@Override
	public String key() {
		return key;
	}

	@Override
	@Nullable
	public V optionalValue() {
		return value;
	}

	@Override
	public String toString() {
		return value == null ? ("Ref[" + key + "]") : value.toString();
	}
}
