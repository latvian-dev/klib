package dev.latvian.mods.klib.registry;

import org.jetbrains.annotations.Nullable;

public interface WithValue<V> {
	@Nullable
	V optionalValue();

	default V value() {
		var value = optionalValue();

		if (value == null) {
			if (this instanceof WithKey<?> withKey) {
				var key = withKey.key();
				throw new NullPointerException("Value of " + key.registry() + "/" + key.identifier() + " isn't bound");
			} else {
				throw new NullPointerException("Value of " + this + " isn't bound");
			}
		}

		return value;
	}
}
