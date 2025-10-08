package dev.latvian.mods.klib.math;

import net.minecraft.world.phys.AABB;

public interface FrustumCheck {
	boolean isVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

	default boolean isVisible(AABB aabb) {
		return aabb.isInfinite() || isVisible(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
	}

	default boolean isVisible(double dx, double dy, double dz, AABB aabb) {
		return aabb.isInfinite() || isVisible(dx + aabb.minX, dy + aabb.minY, dz + aabb.minZ, dx + aabb.maxX, dy + aabb.maxY, dz + aabb.maxZ);
	}
}
