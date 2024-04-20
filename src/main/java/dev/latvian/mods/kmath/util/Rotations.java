package dev.latvian.mods.kmath.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public interface Rotations {
	/**
	 * Calculates the minecraft yaw angle in degrees for the given x and z delta values
	 * Note: Default facing is south for vanilla entities(0)
	 *
	 * @param x x delta
	 * @param z z delta
	 * @return angle in degree
	 */
	static float getYaw(double x, double z) {
		// Minecraft yaw is always offset by 90 degree
		return MathHelper.wrapDegrees((float) (Math.toDegrees(Math.atan2(z, x)) - 90));
	}

	static float getYaw(Vec3d direction) {
		return getYaw(direction.getX(), direction.getZ());
	}

	static float getYaw(BlockPos direction) {
		return getYaw(direction.getX(), direction.getZ());
	}

	/**
	 * Calculates the minecraft pitch angle in degrees for the given y delta. Needs to be normalized to 0-1.
	 *
	 * @param y delta 0-1
	 * @return angle in degree
	 */
	static float getPitch(double y) {
		if (y > 1) {
			return Float.NaN;
		}

		// Minecraft pitch is inverted
		return MathHelper.wrapDegrees((float) -Math.toDegrees(Math.asin(y)));
	}

	static float getPitch(Vec3d direction) {
		return getPitch(direction.normalize().y);
	}

	static float getPitch(BlockPos direction) {
		return getPitch(Vec3d.of(direction));
	}

	static Vec3d getVectorFromBodyYaw(Entity entity) {
		return getVector(entity.getPitch(), entity.getBodyYaw());
	}

	static Vec3d getVector(double pitch, double yaw) {
		double p = Math.toRadians(pitch);
		double y = Math.toRadians(-yaw);
		double h = Math.cos(p);
		return new Vec3d(Math.sin(y) * h, -Math.sin(p), Math.cos(y) * h);
	}
}
