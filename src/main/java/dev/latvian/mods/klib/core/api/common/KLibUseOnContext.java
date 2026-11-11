package dev.latvian.mods.klib.core.api.common;

import dev.latvian.mods.klib.core.NoMixinException;
import net.minecraft.world.phys.BlockHitResult;

public interface KLibUseOnContext {
	default BlockHitResult klib$getHitResult() {
		throw new NoMixinException(this);
	}
}
