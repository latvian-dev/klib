package dev.latvian.mods.klib.easing;

import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.math.Vec3f;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface EasingFunction {
	double ease(double x);

	default float ease(float x) {
		return (float) ease((double) x);
	}

	default double easeClamped(double x) {
		return ease(KMath.clamp(x, 0D, 1D));
	}

	default float easeClamped(float x) {
		return (float) ease(KMath.clamp(x, 0D, 1D));
	}

	default double lerp(double t, double a, double b) {
		return KMath.lerp(ease(t), a, b);
	}

	default float lerp(float t, float a, float b) {
		return KMath.lerp(ease(t), a, b);
	}

	default Vec3 lerp(double t, Vec3 a, Vec3 b) {
		var e = ease(t);
		return new Vec3(KMath.lerp(e, a.x, b.x), KMath.lerp(e, a.y, b.y), KMath.lerp(e, a.z, b.z));
	}

	default Vec3f lerp(float t, Vec3f a, Vec3f b) {
		var e = ease(t);
		return Vec3f.of(KMath.lerp(e, a.x(), b.x()), KMath.lerp(e, a.y(), b.y()), KMath.lerp(e, a.z(), b.z()));
	}
}
