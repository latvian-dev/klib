package dev.latvian.mods.klib.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public interface WithKey<V> extends Comparable<WithKey<V>> {
	@Nullable
	ResourceKey<V> optionalKey();

	default ResourceKey<V> key() {
		var key = optionalKey();

		if (key == null) {
			if (this instanceof WithValue<?> withValue) {
				throw new NullPointerException("Value " + withValue.optionalValue() + " doesn't have a key");
			} else {
				throw new NullPointerException("Value " + this + " doesn't have a key");
			}
		}

		return key;
	}

	default Identifier id() {
		return key().identifier();
	}

	@Override
	default int compareTo(@NonNull WithKey<V> o) {
		return id().compareNamespaced(o.id());
	}
}
