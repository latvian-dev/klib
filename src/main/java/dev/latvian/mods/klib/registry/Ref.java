package dev.latvian.mods.klib.registry;

import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.codec.KLibCodecErrors;

public sealed interface Ref<V> extends WithKey, RefOptimizer<V>, WithValue<V> permits UnitType, RefOfKey, RefOfValue {
	DataResult<String> ERROR_NOT_UNIT = KLibCodecErrors.error("Type is not a unit type");

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

	default DataResult<String> unitKeyResult() {
		return ERROR_NOT_UNIT;
	}

	String toString();

	boolean equals(Object other);
}
