package dev.latvian.mods.klib.math;

import dev.latvian.mods.klib.util.Lazy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3fc;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

@SuppressWarnings("ManualMinMaxCalculation")
public interface KMath {
	enum NumberFormat {
		VERY_SHORT("#.#"),
		SHORT("#.##"),
		LONG("#.####");

		private final DecimalFormat format;

		NumberFormat(String format) {
			this.format = new DecimalFormat(format);
			this.format.setRoundingMode(RoundingMode.FLOOR);
		}

		public String format(float value) {
			if (value == (int) value) {
				return Integer.toString((int) value);
			}

			return format.format(value);
		}

		public String format(double value) {
			if (value == (long) value) {
				return Long.toString((long) value);
			}

			return format.format(value);
		}

		public String format(Position pos) {
			return String.format("%s, %s, %s", format(pos.x()), format(pos.y()), format(pos.z()));
		}

		public String format(Vec3f pos) {
			return String.format("%s, %s, %s", format(pos.x()), format(pos.y()), format(pos.z()));
		}

		public String format(Vector3fc pos) {
			return String.format("%s, %s, %s", format(pos.x()), format(pos.y()), format(pos.z()));
		}

		public String format(Vector4fc pos) {
			return String.format("%s, %s, %s, %s", format(pos.x()), format(pos.y()), format(pos.z()), format(pos.w()));
		}
	}

	List<AABB> CLIP_BOX_LIST = List.of(new AABB(-0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D));
	Vec3 CENTER_VEC3 = new Vec3(0.5D, 0.5D, 0.5D);
	Vec3 ONE_VEC3 = new Vec3(1D, 1D, 1D);

	double SQRT_2 = Math.sqrt(2D);
	double TWO_PI = Math.PI * 2D;
	double HALF_PI = Math.PI / 2D;
	double TO_DEG = 180D / Math.PI;
	double TO_RAD = Math.PI / 180D;

	float F_SQRT_2 = (float) SQRT_2;
	float F_PI = (float) Math.PI;
	float F_TWO_PI = (float) TWO_PI;
	float F_HALF_PI = (float) HALF_PI;
	float F_TO_DEG = (float) TO_DEG;
	float F_TO_RAD = (float) TO_RAD;

	Lazy<Vector2ic[]> CACHED_SPIRAL = Lazy.of(() -> {
		var spiral = new Vector2ic[961];

		for (int i = 0; i < spiral.length; i++) {
			spiral[i] = calculateSpiral(i, new Vector2i());
		}

		return spiral;
	});

	static Vec3 vec3(double x, double y, double z) {
		if (x == 0D && y == 0D && z == 0D) {
			return Vec3.ZERO;
		} else if (x == 1D && y == 1D && z == 1D) {
			return ONE_VEC3;
		} else if (x == 0.5D && y == 0.5D && z == 0.5D) {
			return CENTER_VEC3;
		} else {
			return new Vec3(x, y, z);
		}
	}

	static Vec3 vec3(double v) {
		if (v == 0D) {
			return Vec3.ZERO;
		} else if (v == 1D) {
			return ONE_VEC3;
		} else if (v == 0.5D) {
			return CENTER_VEC3;
		} else {
			return new Vec3(v, v, v);
		}
	}

	static String veryShortFormat(float value) {
		return NumberFormat.VERY_SHORT.format(value);
	}

	static String veryShortFormat(double value) {
		return NumberFormat.VERY_SHORT.format(value);
	}

	static String shortFormat(float value) {
		return NumberFormat.SHORT.format(value);
	}

	static String shortFormat(double value) {
		return NumberFormat.SHORT.format(value);
	}

	static String format(float value) {
		return NumberFormat.LONG.format(value);
	}

	static String format(double value) {
		return NumberFormat.LONG.format(value);
	}

	static String format(BlockPos pos) {
		return String.format("%d, %d, %d", pos.getX(), pos.getY(), pos.getZ());
	}

	static String format(Position pos) {
		return NumberFormat.LONG.format(pos);
	}

	static String format(Vector3fc pos) {
		return NumberFormat.LONG.format(pos);
	}

	static String format(Vector4fc pos) {
		return NumberFormat.LONG.format(pos);
	}

	static NumericTag tag(float num) {
		var i = (int) num;

		if (i == num) {
			if (i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE) {
				return ByteTag.valueOf((byte) i);
			}

			if (i >= Short.MIN_VALUE && i <= Short.MAX_VALUE) {
				return ShortTag.valueOf((short) i);
			}

			return IntTag.valueOf(i);
		} else {
			return FloatTag.valueOf(num);
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

	static double lerp(double delta, double min, double max) {
		return min + delta * (max - min);
	}

	static float lerp(float delta, float min, float max) {
		return min + delta * (max - min);
	}

	static double lerp(double delta, double range) {
		return delta * range * 2D - range;
	}

	static float lerp(float delta, float range) {
		return delta * range * 2F - range;
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

	static double min(double a, double b, double c, double d, double e, double f, double g, double h) {
		return Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(a, b), c), d), e), f), Math.min(g, h));
	}

	static double max(double a, double b, double c, double d, double e, double f, double g, double h) {
		return Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(a, b), c), d), e), f), Math.max(g, h));
	}

	static float min(float a, float b, float c, float d, float e, float f, float g, float h) {
		return Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(a, b), c), d), e), f), Math.min(g, h));
	}

	static float max(float a, float b, float c, float d, float e, float f, float g, float h) {
		return Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(a, b), c), d), e), f), Math.max(g, h));
	}

	static BlockPos min(BlockPos a, BlockPos b) {
		return new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()));
	}

	static BlockPos max(BlockPos a, BlockPos b) {
		return new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()));
	}

	static boolean isIdentity(Matrix3fc m) {
		return m.m00() == 1F && m.m01() == 0F && m.m02() == 0F &&
			m.m10() == 0F && m.m11() == 1F && m.m12() == 0F &&
			m.m20() == 0F && m.m21() == 0F && m.m22() == 1F;
	}

	static boolean isIdentity(Matrix4fc m) {
		return m.m00() == 1F && m.m01() == 0F && m.m02() == 0F && m.m03() == 0F &&
			m.m10() == 0F && m.m11() == 1F && m.m12() == 0F && m.m13() == 0F &&
			m.m20() == 0F && m.m21() == 0F && m.m22() == 1F && m.m23() == 0F &&
			m.m30() == 0F && m.m31() == 0F && m.m32() == 0F && m.m33() == 1F;
	}

	static double dist(double x0, double y0, double z0, double x1, double y1, double z1) {
		double dx = x1 - x0;
		double dy = y1 - y0;
		double dz = z1 - z0;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	static double dist(double x0, double y0, double x1, double y1) {
		double dx = x1 - x0;
		double dy = y1 - y0;
		return Math.sqrt(dx * dx + dy * dy);
	}

	static float dist(float x0, float y0, float z0, float x1, float y1, float z1) {
		double dx = x1 - x0;
		double dy = y1 - y0;
		double dz = z1 - z0;
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	static float dist(float x0, float y0, float x1, float y1) {
		double dx = x1 - x0;
		double dy = y1 - y0;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	static Vector2ic getSpiral(int index) {
		if (index <= 0) {
			return Identity.IVEC_2;
		} else if (index < 961) {
			return CACHED_SPIRAL.get()[index];
		} else {
			return calculateSpiral(index, new Vector2i());
		}
	}

	static Vector2i calculateSpiral(int index, Vector2i out) {
		if (index <= 0) {
			return out.set(0);
		}

		int x = 0, y = 0;
		int dx = 0, dy = 1;
		int segmentLength = 1, segmentPassed = 0;

		for (var n = 0; n < index; n++) {
			x += dx;
			y += dy;
			segmentPassed++;

			if (segmentPassed == segmentLength) {
				segmentPassed = 0;

				var buffer = dy;
				dy = -dx;
				dx = buffer;

				if (dx == 0) {
					segmentLength++;
				}
			}
		}

		return out.set(x, y);
	}

	static void bezier(float t, float x1, float y1, float x2, float y2, Vector2f to) {
		float it = 1F - t;
		float a1 = 3F * it * it * t;
		float a2 = 3F * it * t * t;
		float a3 = t * t * t;
		to.x = a1 * x1 + a2 * x2 + a3;
		to.y = a1 * y1 + a2 * y2 + a3;
	}

	static float bezierAxis(float t, float p1, float p2) {
		float it = 1F - t;
		float a1 = 3F * it * it * t;
		float a2 = 3F * it * t * t;
		float a3 = t * t * t;
		return a1 * p1 + a2 * p2 + a3;
	}

	static float linearizedBezierY(float t, float x1, float y1, float x2, float y2, int precision1, int precision2) {
		float ax = 3F * x1 - 3F * x2 + 1F;
		float bx = -6F * x1 + 3F * x2;
		float cx = 3F * x1;

		float ay = 3F * y1 - 3F * y2 + 1F;
		float by = -6F * y1 + 3F * y2;
		float cy = 3F * y1;

		float tt = t;
		boolean ok = true;

		for (int i = 0; i < precision1; i++) {
			float xt = ((ax * tt + bx) * tt + cx) * tt;
			float dxt = (3F * ax * tt + 2F * bx) * tt + cx;
			float diff = xt - t;

			if (Math.abs(diff) < 0.000001F) {
				break;
			}

			if (Math.abs(dxt) < 0.000001F) {
				ok = false;
				break;
			}

			float next = tt - diff / dxt;

			if (next < 0F || next > 1F) {
				ok = false;
				break;
			}

			tt = next;
		}

		if (!ok) {
			float lo = 0F;
			float hi = 1F;

			for (int i = 0; i < precision2; i++) {
				float mid = (lo + hi) * 0.5F;
				float xm = ((ax * mid + bx) * mid + cx) * mid;

				if (xm < t) {
					lo = mid;
				} else {
					hi = mid;
				}
			}

			tt = (lo + hi) * 0.5F;
		}

		return ((ay * tt + by) * tt + cy) * tt;
	}

	static float linearizedBezierY(float t, float x1, float y1, float x2, float y2) {
		return linearizedBezierY(t, x1, y1, x2, y2, 8, 16);
	}
}