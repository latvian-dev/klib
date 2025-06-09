package dev.latvian.mods.klib.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.joml.Matrix2d;
import org.joml.Matrix2dc;
import org.joml.Matrix2f;
import org.joml.Matrix2fc;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.Vector4d;
import org.joml.Vector4dc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.joml.Vector4i;
import org.joml.Vector4ic;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
public interface JOMLCodecs {
	Codec<Vector2f> VEC2 = Codec.FLOAT.listOf(2, 2).xmap(l -> new Vector2f(l.get(0), l.get(1)), v -> List.of(v.x, v.y));
	Codec<Vector3f> VEC3 = Codec.FLOAT.listOf(3, 3).xmap(l -> new Vector3f(l.get(0), l.get(1), l.get(2)), v -> List.of(v.x, v.y, v.z));
	Codec<Vector4f> VEC4 = Codec.FLOAT.listOf(3, 4).xmap(l -> new Vector4f(l.get(0), l.get(1), l.get(2), l.size() >= 4 ? l.get(3) : 1F), v -> v.w == 1F ? List.of(v.x, v.y, v.z) : List.of(v.x, v.y, v.z, v.w));

	Codec<Vector2f> VEC2S = Codec.either(Codec.FLOAT, VEC2).xmap(either -> either.map(Vector2f::new, Function.identity()), v -> v.x == v.y ? Either.left(v.x) : Either.right(v));
	Codec<Vector3f> VEC3S = Codec.either(Codec.FLOAT, VEC3).xmap(either -> either.map(Vector3f::new, Function.identity()), v -> v.x == v.y && v.x == v.z ? Either.left(v.x) : Either.right(v));
	Codec<Vector4f> VEC4S = Codec.either(Codec.FLOAT, VEC4).xmap(either -> either.map(v -> new Vector4f(v, v, v, 1F), Function.identity()), v -> v.x == v.y && v.x == v.z && v.w == 1F ? Either.left(v.x) : Either.right(v));

	Codec<Quaternionf> QUATERNION = Codec.FLOAT.listOf(4, 4).xmap(l -> new Quaternionf(l.get(0), l.get(1), l.get(2), l.get(3)), v -> List.of(v.x, v.y, v.z, v.w));

	Codec<Matrix2f> MAT2 = Codec.FLOAT.listOf(4, 4).xmap(l -> new Matrix2f(
		l.get(0), l.get(1),
		l.get(2), l.get(3)
	), m -> List.of(
		m.m00(), m.m01(),
		m.m10(), m.m11()
	));

	Codec<Matrix3f> MAT3 = Codec.FLOAT.listOf(9, 9).xmap(l -> new Matrix3f(
		l.get(0), l.get(1), l.get(2),
		l.get(3), l.get(4), l.get(5),
		l.get(6), l.get(7), l.get(8)
	), m -> List.of(
		m.m00(), m.m01(), m.m02(),
		m.m10(), m.m11(), m.m12(),
		m.m20(), m.m21(), m.m22()
	));

	Codec<Matrix4f> MAT4 = Codec.FLOAT.listOf(16, 16).xmap(l -> new Matrix4f(
		l.get(0), l.get(1), l.get(2), l.get(3),
		l.get(4), l.get(5), l.get(6), l.get(7),
		l.get(8), l.get(9), l.get(10), l.get(11),
		l.get(12), l.get(13), l.get(14), l.get(15)
	), m -> List.of(
		m.m00(), m.m01(), m.m02(), m.m03(),
		m.m10(), m.m11(), m.m12(), m.m13(),
		m.m20(), m.m21(), m.m22(), m.m23(),
		m.m30(), m.m31(), m.m32(), m.m33()
	));

	Codec<Vector2d> DVEC2 = Codec.DOUBLE.listOf(2, 2).xmap(l -> new Vector2d(l.get(0), l.get(1)), v -> List.of(v.x, v.y));
	Codec<Vector3d> DVEC3 = Codec.DOUBLE.listOf(3, 3).xmap(l -> new Vector3d(l.get(0), l.get(1), l.get(2)), v -> List.of(v.x, v.y, v.z));
	Codec<Vector4d> DVEC4 = Codec.DOUBLE.listOf(3, 4).xmap(l -> new Vector4d(l.get(0), l.get(1), l.get(2), l.size() >= 4 ? l.get(3) : 1F), v -> v.w == 1D ? List.of(v.x, v.y, v.z) : List.of(v.x, v.y, v.z, v.w));

	Codec<Vector2d> DVEC2S = Codec.either(Codec.DOUBLE, DVEC2).xmap(e -> e.map(v -> new Vector2d(v, v), Function.identity()), v -> v.x == v.y ? Either.left(v.x) : Either.right(v));
	Codec<Vector3d> DVEC3S = Codec.either(Codec.DOUBLE, DVEC3).xmap(e -> e.map(v -> new Vector3d(v, v, v), Function.identity()), v -> v.x == v.y && v.x == v.z ? Either.left(v.x) : Either.right(v));
	Codec<Vector4d> DVEC4S = Codec.either(Codec.DOUBLE, DVEC4).xmap(e -> e.map(v -> new Vector4d(v, v, v, 1D), Function.identity()), v -> v.x == v.y && v.x == v.z && v.w == 1D ? Either.left(v.x) : Either.right(v));

	Codec<Quaterniond> DQUATERNION = Codec.DOUBLE.listOf(4, 4).xmap(l -> new Quaterniond(l.get(0), l.get(1), l.get(2), l.get(3)), v -> List.of(v.x, v.y, v.z, v.w));

	Codec<Matrix2d> DMAT2 = Codec.DOUBLE.listOf(4, 4).xmap(l -> new Matrix2d(
		l.get(0), l.get(1),
		l.get(2), l.get(3)
	), m -> List.of(
		m.m00(), m.m01(),
		m.m10(), m.m11()
	));

	Codec<Matrix3d> DMAT3 = Codec.DOUBLE.listOf(9, 9).xmap(l -> new Matrix3d(
		l.get(0), l.get(1), l.get(2),
		l.get(3), l.get(4), l.get(5),
		l.get(6), l.get(7), l.get(8)
	), m -> List.of(
		m.m00(), m.m01(), m.m02(),
		m.m10(), m.m11(), m.m12(),
		m.m20(), m.m21(), m.m22()
	));

	Codec<Matrix4d> DMAT4 = Codec.DOUBLE.listOf(16, 16).xmap(l -> new Matrix4d(
		l.get(0), l.get(1), l.get(2), l.get(3),
		l.get(4), l.get(5), l.get(6), l.get(7),
		l.get(8), l.get(9), l.get(10), l.get(11),
		l.get(12), l.get(13), l.get(14), l.get(15)
	), m -> List.of(
		m.m00(), m.m01(), m.m02(), m.m03(),
		m.m10(), m.m11(), m.m12(), m.m13(),
		m.m20(), m.m21(), m.m22(), m.m23(),
		m.m30(), m.m31(), m.m32(), m.m33()
	));

	Codec<Vector2i> IVEC2 = Codec.INT.listOf(2, 2).xmap(l -> new Vector2i(l.get(0), l.get(1)), v -> List.of(v.x, v.y));
	Codec<Vector3i> IVEC3 = Codec.INT.listOf(3, 3).xmap(l -> new Vector3i(l.get(0), l.get(1), l.get(2)), v -> List.of(v.x, v.y, v.z));
	Codec<Vector4i> IVEC4 = Codec.INT.listOf(3, 4).xmap(l -> new Vector4i(l.get(0), l.get(1), l.get(2), l.size() >= 4 ? l.get(3) : 1), v -> v.w == 1 ? List.of(v.x, v.y, v.z) : List.of(v.x, v.y, v.z, v.w));

	Codec<Vector2i> IVEC2S = Codec.either(Codec.INT, IVEC2).xmap(e -> e.map(v -> new Vector2i(v, v), Function.identity()), v -> v.x == v.y ? Either.left(v.x) : Either.right(v));
	Codec<Vector3i> IVEC3S = Codec.either(Codec.INT, IVEC3).xmap(e -> e.map(v -> new Vector3i(v, v, v), Function.identity()), v -> v.x == v.y && v.x == v.z ? Either.left(v.x) : Either.right(v));
	Codec<Vector4i> IVEC4S = Codec.either(Codec.INT, IVEC4).xmap(e -> e.map(v -> new Vector4i(v, v, v, 1), Function.identity()), v -> v.x == v.y && v.x == v.z && v.w == 1 ? Either.left(v.x) : Either.right(v));

	Codec<Vector2fc> VEC2C = VEC2.xmap(Function.identity(), v -> v instanceof Vector2f c ? c : new Vector2f(v));
	Codec<Vector3fc> VEC3C = VEC3.xmap(Function.identity(), v -> v instanceof Vector3f c ? c : new Vector3f(v));
	Codec<Vector4fc> VEC4C = VEC4.xmap(Function.identity(), v -> v instanceof Vector4f c ? c : new Vector4f(v));
	Codec<Vector2fc> VEC2SC = VEC2S.xmap(Function.identity(), v -> v instanceof Vector2f c ? c : new Vector2f(v));
	Codec<Vector3fc> VEC3SC = VEC3S.xmap(Function.identity(), v -> v instanceof Vector3f c ? c : new Vector3f(v));
	Codec<Vector4fc> VEC4SC = VEC4S.xmap(Function.identity(), v -> v instanceof Vector4f c ? c : new Vector4f(v));
	Codec<Quaternionfc> QUATERNIONC = QUATERNION.xmap(Function.identity(), v -> v instanceof Quaternionf c ? c : new Quaternionf(v));
	Codec<Matrix2fc> MAT2C = MAT2.xmap(Function.identity(), v -> v instanceof Matrix2f c ? c : new Matrix2f(v));
	Codec<Matrix3fc> MAT3C = MAT3.xmap(Function.identity(), v -> v instanceof Matrix3f c ? c : new Matrix3f(v));
	Codec<Matrix4fc> MAT4C = MAT4.xmap(Function.identity(), v -> v instanceof Matrix4f c ? c : new Matrix4f(v));
	Codec<Vector2dc> DVEC2C = DVEC2.xmap(Function.identity(), v -> v instanceof Vector2d c ? c : new Vector2d(v));
	Codec<Vector3dc> DVEC3C = DVEC3.xmap(Function.identity(), v -> v instanceof Vector3d c ? c : new Vector3d(v));
	Codec<Vector4dc> DVEC4C = DVEC4.xmap(Function.identity(), v -> v instanceof Vector4d c ? c : new Vector4d(v));
	Codec<Vector2dc> DVEC2SC = DVEC2S.xmap(Function.identity(), v -> v instanceof Vector2d c ? c : new Vector2d(v));
	Codec<Vector3dc> DVEC3SC = DVEC3S.xmap(Function.identity(), v -> v instanceof Vector3d c ? c : new Vector3d(v));
	Codec<Vector4dc> DVEC4SC = DVEC4S.xmap(Function.identity(), v -> v instanceof Vector4d c ? c : new Vector4d(v));
	Codec<Quaterniondc> DQUATERNIONC = DQUATERNION.xmap(Function.identity(), v -> v instanceof Quaterniond c ? c : new Quaterniond(v));
	Codec<Matrix2dc> DMAT2C = DMAT2.xmap(Function.identity(), v -> v instanceof Matrix2d c ? c : new Matrix2d(v));
	Codec<Matrix3dc> DMAT3C = DMAT3.xmap(Function.identity(), v -> v instanceof Matrix3d c ? c : new Matrix3d(v));
	Codec<Matrix4dc> DMAT4C = DMAT4.xmap(Function.identity(), v -> v instanceof Matrix4d c ? c : new Matrix4d(v));
	Codec<Vector2ic> IVEC2C = IVEC2.xmap(Function.identity(), v -> v instanceof Vector2i c ? c : new Vector2i(v));
	Codec<Vector3ic> IVEC3C = IVEC3.xmap(Function.identity(), v -> v instanceof Vector3i c ? c : new Vector3i(v));
	Codec<Vector4ic> IVEC4C = IVEC4.xmap(Function.identity(), v -> v instanceof Vector4i c ? c : new Vector4i(v));
	Codec<Vector2ic> IVEC2SC = IVEC2S.xmap(Function.identity(), v -> v instanceof Vector2i c ? c : new Vector2i(v));
	Codec<Vector3ic> IVEC3SC = IVEC3S.xmap(Function.identity(), v -> v instanceof Vector3i c ? c : new Vector3i(v));
	Codec<Vector4ic> IVEC4SC = IVEC4S.xmap(Function.identity(), v -> v instanceof Vector4i c ? c : new Vector4i(v));
}
