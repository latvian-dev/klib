package dev.latvian.mods.klib.math;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

public interface Vec3f {
	Vector3fc ZERO = new Vector3f(0F, 0F, 0F);
	Vector3fc ONE = new Vector3f(1F, 1F, 1F);

	Vector3fc DOWN = new Vector3f(0F, -1F, 0F);
	Vector3fc UP = new Vector3f(0F, 1F, 0F);
	Vector3fc NORTH = new Vector3f(0F, 0F, -1F);
	Vector3fc SOUTH = new Vector3f(0F, 0F, 1F);
	Vector3fc WEST = new Vector3f(-1F, 0F, 0F);
	Vector3fc EAST = new Vector3f(1F, 0F, 0F);

	Vector3fc[] DIRECTIONS = {DOWN, UP, NORTH, SOUTH, WEST, EAST};

	static Vector3fc of(float x, float y, float z) {
		if (x == 0F && y == 0F && z == 0F) {
			return ZERO;
		} else if (x == 1F && y == 1F && z == 1F) {
			return ONE;
		} else if (x == 0F && y == -1F && z == 0F) {
			return DOWN;
		} else if (x == 0F && y == 1F && z == 0F) {
			return UP;
		} else if (x == 0F && y == 0F && z == -1F) {
			return NORTH;
		} else if (x == 0F && y == 0F && z == 1F) {
			return SOUTH;
		} else if (x == -1F && y == 0F && z == 0F) {
			return WEST;
		} else if (x == 1F && y == 0F && z == 0F) {
			return EAST;
		} else {
			return new Vector3f(x, y, z);
		}
	}

	static Vector3fc of(double x, double y, double z) {
		return of((float) x, (float) y, (float) z);
	}

	static Vector3fc of(Vec3 v) {
		return of((float) v.x, (float) v.y, (float) v.z);
	}

	static Vector3fc of(Vector4fc v) {
		return of(v.x(), v.y(), v.z());
	}

	static Vector3fc of(float v) {
		if (v == 0F) {
			return ZERO;
		} else if (v == 1F) {
			return ONE;
		} else {
			return of(v, v, v);
		}
	}
}
