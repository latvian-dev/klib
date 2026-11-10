package dev.latvian.mods.klib.core.api.client;

import dev.latvian.mods.klib.core.NoMixinException;
import dev.latvian.mods.klib.math.Line;
import net.minecraft.world.phys.Vec3;

public interface KLibCamera {
	default void klib$setPosition(Vec3 pos) {
	}

	default Line klib$ray(double distance) {
		throw new NoMixinException(this);
	}
}
