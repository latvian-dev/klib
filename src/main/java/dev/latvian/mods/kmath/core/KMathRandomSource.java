package dev.latvian.mods.kmath.core;

import dev.latvian.mods.kmath.KMath;
import dev.latvian.mods.kmath.Vec2d;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface KMathRandomSource {
	default RandomSource kmath$self() {
		return (RandomSource) this;
	}

	default float nextRange(float min, float max) {
		return KMath.lerp(kmath$self().nextFloat(), min, max);
	}

	default double nextRange(double min, double max) {
		return KMath.lerp(kmath$self().nextFloat(), min, max);
	}

	default float nextRange(float range) {
		return KMath.lerp(kmath$self().nextFloat(), range);
	}

	default double nextRange(double range) {
		return KMath.lerp(kmath$self().nextFloat(), range);
	}

	default Vec3 nextSpherePosition() {
		var phi = nextRange(0D, Math.PI * 2D);
		var theta = Math.acos(nextRange(1D));
		var sinTheta = Math.sin(theta);
		var x = sinTheta * Math.cos(phi);
		var y = Math.cos(theta);
		var z = sinTheta * Math.sin(phi);
		return new Vec3(x, y, z);
	}

	default Vec3 nextOuterSpherePosition() {
		return nextSpherePosition().normalize();
	}

	default Vec2d nextCirclePosition() {
		var phi = nextRange(0D, Math.PI * 2D);
		var theta = Math.acos(nextRange(1D));
		var sinTheta = Math.sin(theta);
		var x = sinTheta * Math.cos(phi);
		var y = sinTheta * Math.sin(phi);
		return new Vec2d(x, y);
	}

	default Vec2d nextOuterCirclePosition() {
		var phi = nextRange(0D, Math.PI * 2D);
		var x = Math.cos(phi);
		var y = Math.sin(phi);
		return new Vec2d(x, y);
	}

	default Vec3 nextCuboidPosition(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		var x = nextRange(minX, maxX);
		var y = nextRange(minY, maxY);
		var z = nextRange(minZ, maxZ);
		return new Vec3(x, y, z);
	}

	default Vec3 nextCuboidPosition(double rangeX, double rangeY, double rangeZ) {
		var x = nextRange(rangeX);
		var y = nextRange(rangeY);
		var z = nextRange(rangeZ);
		return new Vec3(x, y, z);
	}

	default Vec3 nextCuboidPosition(AABB box) {
		var x = nextRange(box.minX, box.maxX);
		var y = nextRange(box.minY, box.maxY);
		var z = nextRange(box.minZ, box.maxZ);
		return new Vec3(x, y, z);
	}
}
