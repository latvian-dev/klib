package dev.latvian.mods.klib.registry;

import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public sealed interface Ref<V> extends WithKey<V>, WithValue<V> permits CustomRegistryType.Unit, Ref.OfKey, Ref.OfValue {
	final class OfKey<V> implements Ref<V> {
		private final ResourceKey<V> key;
		V value;

		OfKey(ResourceKey<V> key) {
			this.key = key;
			this.value = null;
		}

		@Override
		public ResourceKey<V> optionalKey() {
			return key;
		}

		@Override
		public ResourceKey<V> key() {
			return key;
		}

		@Override
		@Nullable
		public V optionalValue() {
			return value;
		}

		@Override
		public String toString() {
			return value == null ? ("Ref[" + key.identifier() + "]") : value.toString();
		}
	}

	final class OfValue<V> implements Ref<V> {
		ResourceKey<V> key;
		private final V value;

		OfValue(V value) {
			this.key = null;
			this.value = value;
		}

		@Override
		@Nullable
		public ResourceKey<V> optionalKey() {
			return key;
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
			throw new NullPointerException("Value of " + key.registry() + "/" + key.identifier() + " isn't bound");
		}

		return value;
	}
}
