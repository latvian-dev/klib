package dev.latvian.mods.klib.math;

import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface RandomUtils {
	static boolean roll(RandomSource random, float chance) {
		if (chance <= 0F) {
			return false;
		} else if (chance >= 1F) {
			return true;
		} else {
			return random.nextFloat() < chance;
		}
	}

	static float nextRange(RandomSource random, float min, float max) {
		return KMath.lerp(random.nextFloat(), min, max);
	}

	static double nextRange(RandomSource random, double min, double max) {
		return KMath.lerp(random.nextFloat(), min, max);
	}

	static float nextRange(RandomSource random, float range) {
		return KMath.lerp(random.nextFloat(), range);
	}

	static double nextRange(RandomSource random, double range) {
		return KMath.lerp(random.nextFloat(), range);
	}

	static Vec3 nextSpherePosition(RandomSource random) {
		var phi = nextRange(random, 0D, Math.PI * 2D);
		var theta = Math.acos(nextRange(random, 1D));
		var sinTheta = Math.sin(theta);
		var x = sinTheta * Math.cos(phi);
		var y = Math.cos(theta);
		var z = sinTheta * Math.sin(phi);
		return new Vec3(x, y, z);
	}

	static Vec3 nextOuterSpherePosition(RandomSource random) {
		return nextSpherePosition(random).normalize();
	}

	static Vec2d nextCirclePosition(RandomSource random) {
		var phi = nextRange(random, 0D, Math.PI * 2D);
		var theta = Math.acos(nextRange(random, 1D));
		var sinTheta = Math.sin(theta);
		var x = sinTheta * Math.cos(phi);
		var y = sinTheta * Math.sin(phi);
		return new Vec2d(x, y);
	}

	static Vec2d nextOuterCirclePosition(RandomSource random) {
		var phi = nextRange(random, 0D, Math.PI * 2D);
		var x = Math.cos(phi);
		var y = Math.sin(phi);
		return new Vec2d(x, y);
	}

	static Vec3 nextCuboidPosition(RandomSource random, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		var x = nextRange(random, minX, maxX);
		var y = nextRange(random, minY, maxY);
		var z = nextRange(random, minZ, maxZ);
		return new Vec3(x, y, z);
	}

	static Vec3 nextCuboidPosition(RandomSource random, double rangeX, double rangeY, double rangeZ) {
		var x = nextRange(random, rangeX);
		var y = nextRange(random, rangeY);
		var z = nextRange(random, rangeZ);
		return new Vec3(x, y, z);
	}

	static Vec3 nextCuboidPosition(RandomSource random, AABB box) {
		var x = nextRange(random, box.minX, box.maxX);
		var y = nextRange(random, box.minY, box.maxY);
		var z = nextRange(random, box.minZ, box.maxZ);
		return new Vec3(x, y, z);
	}
}
