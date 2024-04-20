package dev.latvian.mods.kmath.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Rotations {
	/**
	 * Calculates the minecraft yaw angle in degrees for the given x and z delta values
	 * Note: Default facing is south for vanilla entities(0)
	 *
	 * @param x x delta
	 * @param z z delta
	 * @return angle in degree
	 */
	public static float getYaw(double x, double z) {
		// Minecraft yaw is always offset by 90 degree
		return MathHelper.wrapDegrees((float) (Math.toDegrees(Math.atan2(z, x)) - 90));
	}

	public static float getYaw(Vec3d direction) {
		return getYaw(direction.getX(), direction.getZ());
	}

	public static float getYaw(BlockPos direction) {
		return getYaw(direction.getX(), direction.getZ());
	}

	/**
	 * Calculates the minecraft pitch angle in degrees for the given y delta. Needs to be normalized to 0-1.
	 *
	 * @param y delta 0-1
	 * @return angle in degree
	 */
	public static float getPitch(double y) {
		if (y > 1) {
			return Float.NaN;
		}

		// Minecraft pitch is inverted
		return MathHelper.wrapDegrees((float) -Math.toDegrees(Math.asin(y)));
	}

	public static float getPitch(Vec3d direction) {
		return getPitch(direction.normalize().y);
	}

	public static float getPitch(BlockPos direction) {
		return getPitch(Vec3d.of(direction));
	}
}
