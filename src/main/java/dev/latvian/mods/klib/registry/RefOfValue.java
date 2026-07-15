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

	@Override
	public int hashCode() {
		return System.identityHashCode(value);
	}

	@Override
	@SuppressWarnings("StringEquality")
	public boolean equals(Object obj) {
		if (obj instanceof Ref<?> ref) {
			return value == ref.optionalValue();
		} else {
			return false;
		}
	}
}
