package dev.latvian.mods.klib.registry;

public final class RefOfValue<V> implements Ref<V> {
	private final V value;

	RefOfValue(V value) {
		this.value = value;
	}

	@Override
	public String optionalKey() {
		return "";
	}

	@Override
	public V optionalValue() {
		return value;
	}

	@Override
	public V value() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
