package dev.latvian.mods.klib.core.api.common;

import dev.latvian.mods.klib.core.NoMixinException;

public interface KLibFoodData {
	default float klib$getExhaustionLevel() {
		throw new NoMixinException(this);
	}

	default void klib$setExhaustionLevel(float value) {
		throw new NoMixinException(this);
	}

	default int klib$getTickTimer() {
		throw new NoMixinException(this);
	}

	default void klib$setTickTimer(int value) {
		throw new NoMixinException(this);
	}
}
