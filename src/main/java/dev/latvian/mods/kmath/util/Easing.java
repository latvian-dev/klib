package dev.latvian.mods.kmath.util;

import dev.latvian.mods.kmath.KMath;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import net.minecraft.util.math.Vec3d;

@FunctionalInterface
public interface Easing extends Double2DoubleFunction {
	double ease(double x);

	default double easeClamped(double x) {
		return ease(KMath.clamp(x, 0D, 1D));
	}

	@Override
	default double get(double x) {
		return ease(x);
	}

	default double lerp(double t, double a, double b) {
		return a + get(t) * (b - a);
	}

	default Vec3d lerp(double t, Vec3d a, Vec3d b) {
		return new Vec3d(lerp(t, a.x, b.x), lerp(t, a.y, b.y), lerp(t, a.z, b.z));
	}

	Easing LINEAR = x -> x;
	Easing SMOOTHSTEP = KMath::smoothstep;
	Easing ISMOOTHSTEP = KMath::ismoothstep;
	Easing SMOOTHERSTEP = KMath::smootherstep;

	Easing SINE_IN = x -> 1 - Math.cos((x - Math.PI) / 2);
	Easing SINE_OUT = x -> Math.sin((x * Math.PI) / 2);
	Easing SINE_IN_OUT = x -> -(Math.cos(Math.PI * x) - 1) / 2;

	Easing QUAD_IN = x -> x * x;
	Easing QUAD_OUT = x -> 1 - (1 - x) * (1 - x);
	Easing QUAD_IN_OUT = x -> x < 0.5 ? 2 * x * x : 1 - Math.pow(-2 * x + 2, 2) / 2;

	Easing CUBIC_IN = x -> x * x * x;
	Easing CUBIC_OUT = x -> 1 - Math.pow(1 - x, 3);
	Easing CUBIC_IN_OUT = x -> x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2D;

	Easing QUART_IN = x -> x * x * x * x;
	Easing QUART_OUT = x -> 1 - Math.pow(1 - x, 4);
	Easing QUART_IN_OUT = x -> x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2D;

	Easing QUINT_IN = x -> x * x * x * x * x;
	Easing QUINT_OUT = x -> 1 - Math.pow(1 - x, 5);
	Easing QUINT_IN_OUT = x -> x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2D;

	Easing EXPO_IN = x -> x == 0 ? 0 : Math.pow(2, 10 * x - 10);
	Easing EXPO_OUT = x -> x == 1 ? 1 : 1 - Math.pow(2, -10 * x);
	Easing EXPO_IN_OUT = x -> x == 0 ? 0 : x == 1 ? 1 : x < 0.5 ? Math.pow(2, 20 * x - 10) / 2 : (2 - Math.pow(2, -20 * x + 10)) / 2;

	Easing CIRC_IN = x -> 1 - Math.sqrt(1 - x * x);
	Easing CIRC_OUT = x -> Math.sqrt(1 - (x - 1) * (x - 1));
	Easing CIRC_IN_OUT = x -> x < 0.5 ? (1 - Math.sqrt(1 - 4 * x * x)) / 2 : (Math.sqrt(1 - (-2 * x + 2) * (-2 * x + 2)) + 1) / 2;

	Easing BACK_IN = x -> x * x * (2.70158 * x - 1.70158);
	Easing BACK_OUT = x -> 1 - (1 - x) * (1 - x) * (2.70158 * (1 - x) - 1.70158);
	Easing BACK_IN_OUT = x -> x < 0.5 ? Math.pow(2 * x, 2) * ((2.5949095 + 1) * 2 * x - 2.5949095) / 2 : (Math.pow(2 * x - 2, 2) * ((2.5949095 + 1) * (x * 2 - 2) + 2.5949095) + 2) / 2;

	Easing ELASTIC_IN = x -> Math.sin(13 * Math.PI / 2 * x) * Math.pow(2, 10 * x - 10);
	Easing ELASTIC_OUT = x -> Math.sin(-13 * Math.PI / 2 * (x + 1)) * Math.pow(2, -10 * x) + 1;
	Easing ELASTIC_IN_OUT = x -> x < 0.5 ? Math.sin(13 * Math.PI / 2 * (2 * x)) * Math.pow(2, 10 * (2 * x) - 10) / 2 : Math.sin(-13 * Math.PI / 2 * (2 * x - 1)) * Math.pow(2, -10 * (2 * x - 1)) / 2 + 1;

	Easing BOUNCE_OUT = x -> {
		if (x < 1 / 2.75) {
			return 7.5625 * x * x;
		} else if (x < 2 / 2.75) {
			return 7.5625 * (x -= 1.5 / 2.75) * x + 0.75;
		} else if (x < 2.5 / 2.75) {
			return 7.5625 * (x -= 2.25 / 2.75) * x + 0.9375;
		} else {
			return 7.5625 * (x -= 2.625 / 2.75) * x + 0.984375;
		}
	};

	Easing BOUNCE_IN = x -> 1 - BOUNCE_OUT.ease(1 - x);
	Easing BOUNCE_IN_OUT = x -> x < 0.5 ? BOUNCE_IN.ease(x * 2) / 2 : BOUNCE_OUT.ease(x * 2 - 1) / 2 + 0.5;
}
