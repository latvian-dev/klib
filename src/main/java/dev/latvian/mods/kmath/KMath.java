package dev.latvian.mods.kmath;

import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtShort;

@SuppressWarnings("ManualMinMaxCalculation")
public interface KMath {

	static String format(float value) {
		if (value == (int) value) {
			return Integer.toString((int) value);
		}

		return Float.toString(value);
	}

	static AbstractNbtNumber efficient(float num) {
		var i = (int) num;

		if (i == num) {
			if (i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE) {
				return NbtByte.of((byte) i);
			}

			if (i >= Short.MIN_VALUE && i <= Short.MAX_VALUE) {
				return NbtShort.of((short) i);
			}

			return NbtInt.of(i);
		} else {
			return NbtFloat.of(num);
		}
	}

	static double sq(double t) {
		return t * t;
	}

	static float sq(float t) {
		return t * t;
	}

	static int floor(double value) {
		int i = (int) value;
		return value < i ? i - 1 : i;
	}

	static long lfloor(double value) {
		long i = (long) value;
		return value < i ? i - 1L : i;
	}

	static int floor(float value) {
		int i = (int) value;
		return value < i ? i - 1 : i;
	}

	static int ceil(double value) {
		int i = (int) value;
		return value > i ? i + 1 : i;
	}

	static long lceil(double value) {
		long i = (long) value;
		return value > i ? i + 1L : i;
	}

	static int ceil(float value) {
		int i = (int) value;
		return value > i ? i + 1 : i;
	}

	static double clamp(double value, double min, double max) {
		return value < min ? min : value <= max ? value : max;
	}

	static float clamp(float value, float min, float max) {
		return value < min ? min : value <= max ? value : max;
	}

	static int clamp(int value, int min, int max) {
		return value < min ? min : value <= max ? value : max;
	}

	static double lerp(double value, double min, double max) {
		return min + value * (max - min);
	}

	static float lerp(float value, float min, float max) {
		return min + value * (max - min);
	}

	static double clerp(double value, double min, double max) {
		return value < 0D ? min : value > 1D ? max : (min + value * (max - min));
	}

	static float clerp(float value, float min, float max) {
		return value < 0F ? min : value > 1F ? max : (min + value * (max - min));
	}

	static double map(double value, double min0, double max0, double min1, double max1) {
		return min1 + (max1 - min1) * ((value - min0) / (max0 - min0));
	}

	static float map(float value, float min0, float max0, float min1, float max1) {
		return min1 + (max1 - min1) * ((value - min0) / (max0 - min0));
	}

	static double smoothstep(double t) {
		return t * t * (3D - 2D * t);
	}

	static float smoothstep(float t) {
		return t * t * (3F - 2F * t);
	}

	static double ismoothstep(double t) {
		return t + (t - (t * t * (3D - 2D * t)));
	}

	static float ismoothstep(float t) {
		return t + (t - (t * t * (3F - 2F * t)));
	}

	static double smootherstep(double t) {
		return t * t * t * (t * (t * 6D - 15D) + 10D);
	}

	static float smootherstep(float t) {
		return t * t * t * (t * (t * 6F - 15F) + 10F);
	}

	static double curve(double t, double p1x, double p2x, double p3x) {
		double t1 = 1D - t;
		double v = -(p1x - 8D * p2x + p3x) / 6D;
		return t1 * t1 * t1 * p1x + 3D * t * t1 * t1 * v + 3D * t * t * t1 * v + t * t * t * p3x;
	}
}
