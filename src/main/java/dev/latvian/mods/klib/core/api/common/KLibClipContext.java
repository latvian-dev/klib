package dev.latvian.mods.klib.core.api.common;

import dev.latvian.mods.klib.core.NoMixinException;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.shapes.CollisionContext;

public interface KLibClipContext {
	default ClipContext.Block klib$getBlock() {
		throw new NoMixinException(this);
	}

	default ClipContext.Fluid klib$getFluid() {
		throw new NoMixinException(this);
	}

	default CollisionContext klib$getCollisionContext() {
		throw new NoMixinException(this);
	}
}
