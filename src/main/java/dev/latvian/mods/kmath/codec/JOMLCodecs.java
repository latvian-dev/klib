package dev.latvian.mods.kmath.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.joml.Matrix2d;
import org.joml.Matrix2f;
import org.joml.Matrix3d;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.joml.Vector4i;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
public interface JOMLCodecs {
	Codec<Vector2f> VEC_2 = Codec.FLOAT.listOf(2, 2).xmap(l -> new Vector2f(l.get(0), l.get(1)), v -> List.of(v.x, v.y));
	Codec<Vector3f> VEC_3 = Codec.FLOAT.listOf(3, 3).xmap(l -> new Vector3f(l.get(0), l.get(1), l.get(2)), v -> List.of(v.x, v.y, v.z));
	Codec<Vector4f> VEC_4 = Codec.FLOAT.listOf(3, 4).xmap(l -> new Vector4f(l.get(0), l.get(1), l.get(2), l.size() >= 4 ? l.get(3) : 1F), v -> v.w == 1F ? List.of(v.x, v.y, v.z) : List.of(v.x, v.y, v.z, v.w));

	Codec<Vector2f> VEC_2S = Codec.either(Codec.FLOAT, VEC_2).xmap(either -> either.map(Vector2f::new, Function.identity()), v -> v.x == v.y ? Either.left(v.x) : Either.right(v));
	Codec<Vector3f> VEC_3S = Codec.either(Codec.FLOAT, VEC_3).xmap(either -> either.map(Vector3f::new, Function.identity()), v -> v.x == v.y && v.x == v.z ? Either.left(v.x) : Either.right(v));
	Codec<Vector4f> VEC_4S = Codec.either(Codec.FLOAT, VEC_4).xmap(either -> either.map(v -> new Vector4f(v, v, v, 1F), Function.identity()), v -> v.x == v.y && v.x == v.z && v.w == 1F ? Either.left(v.x) : Either.right(v));

	Codec<Quaternionf> QUATERNION = Codec.FLOAT.listOf(4, 4).xmap(l -> new Quaternionf(l.get(0), l.get(1), l.get(2), l.get(3)), v -> List.of(v.x, v.y, v.z, v.w));

	Codec<Matrix2f> MAT_2 = Codec.FLOAT.listOf(4, 4).xmap(l -> new Matrix2f(
		l.get(0), l.get(1),
		l.get(2), l.get(3)
	), m -> List.of(
		m.m00(), m.m01(),
		m.m10(), m.m11()
	));

	Codec<Matrix3f> MAT_3 = Codec.FLOAT.listOf(9, 9).xmap(l -> new Matrix3f(
		l.get(0), l.get(1), l.get(2),
		l.get(3), l.get(4), l.get(5),
		l.get(6), l.get(7), l.get(8)
	), m -> List.of(
		m.m00(), m.m01(), m.m02(),
		m.m10(), m.m11(), m.m12(),
		m.m20(), m.m21(), m.m22()
	));

	Codec<Matrix4f> MAT_4 = Codec.FLOAT.listOf(16, 16).xmap(l -> new Matrix4f(
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

	Codec<Vector2d> DVEC_2 = Codec.DOUBLE.listOf(2, 2).xmap(l -> new Vector2d(l.get(0), l.get(1)), v -> List.of(v.x, v.y));
	Codec<Vector3d> DVEC_3 = Codec.DOUBLE.listOf(3, 3).xmap(l -> new Vector3d(l.get(0), l.get(1), l.get(2)), v -> List.of(v.x, v.y, v.z));
	Codec<Vector4d> DVEC_4 = Codec.DOUBLE.listOf(3, 4).xmap(l -> new Vector4d(l.get(0), l.get(1), l.get(2), l.size() >= 4 ? l.get(3) : 1F), v -> v.w == 1D ? List.of(v.x, v.y, v.z) : List.of(v.x, v.y, v.z, v.w));

	Codec<Vector2d> DVEC_2S = Codec.either(Codec.DOUBLE, DVEC_2).xmap(e -> e.map(v -> new Vector2d(v, v), Function.identity()), v -> v.x == v.y ? Either.left(v.x) : Either.right(v));
	Codec<Vector3d> DVEC_3S = Codec.either(Codec.DOUBLE, DVEC_3).xmap(e -> e.map(v -> new Vector3d(v, v, v), Function.identity()), v -> v.x == v.y && v.x == v.z ? Either.left(v.x) : Either.right(v));
	Codec<Vector4d> DVEC_4S = Codec.either(Codec.DOUBLE, DVEC_4).xmap(e -> e.map(v -> new Vector4d(v, v, v, 1D), Function.identity()), v -> v.x == v.y && v.x == v.z && v.w == 1D ? Either.left(v.x) : Either.right(v));

	Codec<Quaterniond> DQUATERNION = Codec.DOUBLE.listOf(4, 4).xmap(l -> new Quaterniond(l.get(0), l.get(1), l.get(2), l.get(3)), v -> List.of(v.x, v.y, v.z, v.w));

	Codec<Matrix2d> DMAT_2 = Codec.DOUBLE.listOf(4, 4).xmap(l -> new Matrix2d(
		l.get(0), l.get(1),
		l.get(2), l.get(3)
	), m -> List.of(
		m.m00(), m.m01(),
		m.m10(), m.m11()
	));

	Codec<Matrix3d> DMAT_3 = Codec.DOUBLE.listOf(9, 9).xmap(l -> new Matrix3d(
		l.get(0), l.get(1), l.get(2),
		l.get(3), l.get(4), l.get(5),
		l.get(6), l.get(7), l.get(8)
	), m -> List.of(
		m.m00(), m.m01(), m.m02(),
		m.m10(), m.m11(), m.m12(),
		m.m20(), m.m21(), m.m22()
	));

	Codec<Matrix4d> DMAT_4 = Codec.DOUBLE.listOf(16, 16).xmap(l -> new Matrix4d(
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

	Codec<Vector2i> IVEC_2 = Codec.INT.listOf(2, 2).xmap(l -> new Vector2i(l.get(0), l.get(1)), v -> List.of(v.x, v.y));
	Codec<Vector3i> IVEC_3 = Codec.INT.listOf(3, 3).xmap(l -> new Vector3i(l.get(0), l.get(1), l.get(2)), v -> List.of(v.x, v.y, v.z));
	Codec<Vector4i> IVEC_4 = Codec.INT.listOf(3, 4).xmap(l -> new Vector4i(l.get(0), l.get(1), l.get(2), l.size() >= 4 ? l.get(3) : 1), v -> v.w == 1 ? List.of(v.x, v.y, v.z) : List.of(v.x, v.y, v.z, v.w));

	Codec<Vector2i> IVEC_2S = Codec.either(Codec.INT, IVEC_2).xmap(e -> e.map(v -> new Vector2i(v, v), Function.identity()), v -> v.x == v.y ? Either.left(v.x) : Either.right(v));
	Codec<Vector3i> IVEC_3S = Codec.either(Codec.INT, IVEC_3).xmap(e -> e.map(v -> new Vector3i(v, v, v), Function.identity()), v -> v.x == v.y && v.x == v.z ? Either.left(v.x) : Either.right(v));
	Codec<Vector4i> IVEC_4S = Codec.either(Codec.INT, IVEC_4).xmap(e -> e.map(v -> new Vector4i(v, v, v, 1), Function.identity()), v -> v.x == v.y && v.x == v.z && v.w == 1 ? Either.left(v.x) : Either.right(v));
}
