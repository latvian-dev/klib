package dev.latvian.mods.klib.registry;

import java.util.Comparator;

public interface WithKey {
	Comparator<? super WithKey> COMPARATOR = Comparator.comparing(WithKey::optionalKey);

	String optionalKey();

	default String key() {
		var key = optionalKey();

		if (key.isEmpty()) {
			if (this instanceof WithValue<?> withValue) {
				throw new NullPointerException("Value " + withValue.optionalValue() + " doesn't have a key");
			} else {
				throw new NullPointerException("Value " + this + " doesn't have a key");
			}
		}

		return key;
	}
}
