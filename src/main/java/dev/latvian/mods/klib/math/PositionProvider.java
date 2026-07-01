package dev.latvian.mods.klib.math;

import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface PositionProvider<T> {
	Vec3 get(T instance);
}
