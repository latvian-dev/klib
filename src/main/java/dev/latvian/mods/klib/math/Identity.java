package dev.latvian.mods.klib.math;

import org.joml.Matrix2d;
import org.joml.Matrix2dc;
import org.joml.Matrix2f;
import org.joml.Matrix2fc;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix3x2d;
import org.joml.Matrix3x2dc;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3d;
import org.joml.Matrix4x3dc;
import org.joml.Matrix4x3f;
import org.joml.Matrix4x3fc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2L;
import org.joml.Vector2Lc;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3L;
import org.joml.Vector3Lc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.Vector4L;
import org.joml.Vector4Lc;
import org.joml.Vector4d;
import org.joml.Vector4dc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.joml.Vector4i;
import org.joml.Vector4ic;

public interface Identity {
	Vector2f UNSAFE_VEC_2 = new Vector2f();
	Vector3f UNSAFE_VEC_3 = new Vector3f();
	Vector4f UNSAFE_VEC_4 = new Vector4f();
	Matrix2f UNSAFE_MAT_2 = new Matrix2f();
	Matrix3f UNSAFE_MAT_3 = new Matrix3f();
	Matrix3x2f UNSAFE_MAT_3x2 = new Matrix3x2f();
	Matrix4f UNSAFE_MAT_4 = new Matrix4f();
	Matrix4x3f UNSAFE_MAT_4x3 = new Matrix4x3f();
	Quaternionf UNSAFE_QUATERNION = new Quaternionf();

	Vector2fc VEC_2 = UNSAFE_VEC_2;
	Vector3fc VEC_3 = UNSAFE_VEC_3;
	Vector4fc VEC_4 = UNSAFE_VEC_4;
	Matrix2fc MAT_2 = UNSAFE_MAT_2;
	Matrix3fc MAT_3 = UNSAFE_MAT_3;
	Matrix3x2fc MAT_3x2 = UNSAFE_MAT_3x2;
	Matrix4fc MAT_4 = UNSAFE_MAT_4;
	Matrix4x3fc MAT_4x3 = UNSAFE_MAT_4x3;
	Quaternionfc QUATERNION = UNSAFE_QUATERNION;

	Vector2d UNSAFE_DVEC_2 = new Vector2d();
	Vector3d UNSAFE_DVEC_3 = new Vector3d();
	Vector4d UNSAFE_DVEC_4 = new Vector4d();
	Matrix2d UNSAFE_DMAT_2 = new Matrix2d();
	Matrix3d UNSAFE_DMAT_3 = new Matrix3d();
	Matrix3x2d UNSAFE_DMAT_3x2 = new Matrix3x2d();
	Matrix4d UNSAFE_DMAT_4 = new Matrix4d();
	Matrix4x3d UNSAFE_DMAT_4x3 = new Matrix4x3d();
	Quaterniond UNSAFE_DQUATERNION = new Quaterniond();

	Vector2dc DVEC_2 = UNSAFE_DVEC_2;
	Vector3dc DVEC_3 = UNSAFE_DVEC_3;
	Vector4dc DVEC_4 = UNSAFE_DVEC_4;
	Matrix2dc DMAT_2 = UNSAFE_DMAT_2;
	Matrix3dc DMAT_3 = UNSAFE_DMAT_3;
	Matrix3x2dc DMAT_3x2 = UNSAFE_DMAT_3x2;
	Matrix4dc DMAT_4 = UNSAFE_DMAT_4;
	Matrix4x3dc DMAT_4x3 = UNSAFE_DMAT_4x3;
	Quaterniondc DQUATERNION = UNSAFE_DQUATERNION;

	Vector2i UNSAFE_IVEC_2 = new Vector2i();
	Vector3i UNSAFE_IVEC_3 = new Vector3i();
	Vector4i UNSAFE_IVEC_4 = new Vector4i();

	Vector2ic IVEC_2 = UNSAFE_IVEC_2;
	Vector3ic IVEC_3 = UNSAFE_IVEC_3;
	Vector4ic IVEC_4 = UNSAFE_IVEC_4;

	Vector2L UNSAFE_LVEC_2 = new Vector2L();
	Vector3L UNSAFE_LVEC_3 = new Vector3L();
	Vector4L UNSAFE_LVEC_4 = new Vector4L();

	Vector2Lc LVEC_2 = UNSAFE_LVEC_2;
	Vector3Lc LVEC_3 = UNSAFE_LVEC_3;
	Vector4Lc LVEC_4 = UNSAFE_LVEC_4;
}
