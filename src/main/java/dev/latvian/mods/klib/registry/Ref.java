package dev.latvian.mods.klib.registry;

import org.jetbrains.annotations.Nullable;

public sealed interface Ref<V> extends WithKey, WithValue<V> permits UnitType, Ref.OfKey, Ref.OfValue {
	final class OfKey<V> implements Ref<V> {
		private final String key;
		V value;

		OfKey(String key) {
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

	final class OfValue<V> implements Ref<V> {
		private final V value;

		OfValue(V value) {
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
