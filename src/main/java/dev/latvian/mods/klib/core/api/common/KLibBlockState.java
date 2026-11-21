package dev.latvian.mods.klib.core.api.common;

import dev.latvian.mods.klib.block.KLibBlockStateProperties;
import dev.latvian.mods.klib.core.NoMixinException;

public interface KLibBlockState {
	default KLibBlockStateProperties klib$getBlockStateProperties() {
		throw new NoMixinException(this);
	}
}
