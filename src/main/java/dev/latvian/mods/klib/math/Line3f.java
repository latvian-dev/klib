package dev.latvian.mods.klib.math;

import org.joml.Vector3fc;

public record Line3f(Vector3fc start, Vector3fc end) {
	public float dx() {
		return end.x() - start.x();
	}

	public float dy() {
		return end.y() - start.y();
	}

	public float dz() {
		return end.z() - start.z();
	}

	public Vec3f delta() {
		return Vec3f.of(dx(), dy(), dz());
	}
}
