package dev.latvian.mods.klib.math;

import dev.latvian.mods.klib.platform.PlatformHelper;
import net.minecraft.world.phys.AABB;

import java.util.List;

public interface AABBs {
	AABB EMPTY = new AABB(0D, 0D, 0D, 0D, 0D, 0D);
	AABB FULL = new AABB(0D, 0D, 0D, 1D, 1D, 1D);
	AABB FULL_16 = new AABB(0D, 0D, 0D, 16D, 16D, 16D);
	AABB CENTERED = new AABB(-0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D);
	AABB CENTERED_X_PLANE = new AABB(0D, -0.5D, -0.5D, 0D, 0.5D, 0.5D);
	AABB CENTERED_Y_PLANE = new AABB(-0.5D, 0D, -0.5D, 0.5D, 0D, 0.5D);
	AABB CENTERED_Z_PLANE = new AABB(-0.5D, -0.5D, 0D, 0.5D, 0.5D, 0D);
	AABB CENTERED_X_AXIS = new AABB(-0.5D, 0D, 0D, 0.5D, 0D, 0D);
	AABB CENTERED_Y_AXIS = new AABB(0D, -0.5D, 0D, 0D, 0.5D, 0D);
	AABB CENTERED_Z_AXIS = new AABB(0D, 0D, -0.5D, 0D, 0D, 0.5D);
	AABB INFINITE = PlatformHelper.INSTANCE.infiniteAABB();
	AABB WORLD = new AABB(-30000000D, -64D, -30000000D, 30000000D, 320D, 30000000D);

	List<AABB> CLIP_BOX_LIST = List.of(CENTERED);
}
