package dev.latvian.mods.klib.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public interface WithKey<V> {
	Comparator<? super WithKey<?>> COMPARATOR = (o1, o2) -> o1.id().compareNamespaced(o2.id());

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
}
