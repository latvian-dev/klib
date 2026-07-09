package dev.latvian.mods.klib.registry;

public sealed interface Ref<V> extends WithKey, WithRef<V>, WithValue<V> permits UnitType, RefOfKey, RefOfValue {
	@Override
	default Ref<V> ref() {
		return this;
	}

	@Override
	default V value() {
		var value = optionalValue();

		if (value == null) {
			var key = key();
			throw new NullPointerException("Value of " + key + " isn't bound");
		}

		return value;
	}
}
