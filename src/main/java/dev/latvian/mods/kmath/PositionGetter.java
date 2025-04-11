package dev.latvian.mods.kmath;

import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface PositionGetter<T> {
	Vec3 get(T instance);
}
