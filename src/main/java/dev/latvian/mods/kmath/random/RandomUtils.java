package dev.latvian.mods.kmath.random;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class RandomUtils {

	/**
	 * Returns a value between {@code -range} and {@code range}.
	 *
	 * @param random random instance
	 * @param range  maximum value range
	 * @return a value between {@code -range} and {@code range}.
	 */
	public static double range(Random random, double range) {
		return (random.nextDouble() - 0.5) * range * 2;
	}

	public static double range(net.minecraft.util.math.random.Random random, double range) {
		return (random.nextDouble() - 0.5) * range * 2;
	}

	public static double outer(World world, double min, double max) {
		if (world.random.nextDouble() <= 0.5) {
			return min + world.random.nextDouble() * max;
		} else {
			return -(min + world.random.nextDouble() * max);
		}
	}

	public static Vec3d outerVector(World world, double min, double max) {
		double select = min + world.random.nextDouble() * (max - min);
		return new Vec3d(world.random.nextDouble() - 0.5, 0, world.random.nextDouble() - 0.5).normalize().multiply(select, 0, select);
	}
}
