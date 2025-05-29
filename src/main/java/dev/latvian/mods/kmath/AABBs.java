package dev.latvian.mods.kmath;

import net.minecraft.world.phys.AABB;

public interface AABBs {
	AABB FULL = new AABB(0D, 0D, 0D, 1D, 1D, 1D);
	AABB FULL_16 = new AABB(0D, 0D, 0D, 16D, 16D, 16D);
	AABB CENTERED = new AABB(-0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D);
	AABB CENTERED_X_PLANE = new AABB(0D, -0.5D, -0.5D, 0D, 0.5D, 0.5D);
	AABB CENTERED_Y_PLANE = new AABB(-0.5D, 0D, -0.5D, 0.5D, 0D, 0.5D);
	AABB CENTERED_Z_PLANE = new AABB(-0.5D, -0.5D, 0D, 0.5D, 0.5D, 0D);
}
