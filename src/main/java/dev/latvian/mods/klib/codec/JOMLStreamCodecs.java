package dev.latvian.mods.klib.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
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

import java.util.function.Function;

public interface JOMLStreamCodecs {
	StreamCodec<ByteBuf, Vector2f> VEC2 = new StreamCodec<>() {
		@Override
		public Vector2f decode(ByteBuf buf) {
			return new Vector2f(buf.readFloat(), buf.readFloat());
		}

		@Override
		public void encode(ByteBuf buf, Vector2f v) {
			buf.writeFloat(v.x);
			buf.writeFloat(v.y);
		}
	};

	StreamCodec<ByteBuf, Vector3f> VEC3 = new StreamCodec<>() {
		@Override
		public Vector3f decode(ByteBuf buf) {
			return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
		}

		@Override
		public void encode(ByteBuf buf, Vector3f v) {
			buf.writeFloat(v.x);
			buf.writeFloat(v.y);
			buf.writeFloat(v.z);
		}
	};

	StreamCodec<ByteBuf, Vector4f> VEC4 = new StreamCodec<>() {
		@Override
		public Vector4f decode(ByteBuf buf) {
			return new Vector4f(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
		}

		@Override
		public void encode(ByteBuf buf, Vector4f v) {
			buf.writeFloat(v.x);
			buf.writeFloat(v.y);
			buf.writeFloat(v.z);
			buf.writeFloat(v.w);
		}
	};

	StreamCodec<ByteBuf, Quaternionf> QUATERNION = new StreamCodec<>() {
		@Override
		public Quaternionf decode(ByteBuf buf) {
			return new Quaternionf(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
		}

		@Override
		public void encode(ByteBuf buf, Quaternionf v) {
			buf.writeFloat(v.x);
			buf.writeFloat(v.y);
			buf.writeFloat(v.z);
			buf.writeFloat(v.w);
		}
	};

	StreamCodec<ByteBuf, Vector2f> VEC2S = new StreamCodec<>() {
		@Override
		public Vector2f decode(ByteBuf buf) {
			if (buf.readBoolean()) {
				return new Vector2f(buf.readFloat());
			} else {
				return new Vector2f(buf.readFloat(), buf.readFloat());
			}
		}

		@Override
		public void encode(ByteBuf buf, Vector2f v) {
			if (v.x == v.y) {
				buf.writeBoolean(true);
				buf.writeFloat(v.x);
			} else {
				buf.writeBoolean(false);
				buf.writeFloat(v.x);
				buf.writeFloat(v.y);
			}
		}
	};

	StreamCodec<ByteBuf, Vector3f> VEC3S = new StreamCodec<>() {
		@Override
		public Vector3f decode(ByteBuf buf) {
			if (buf.readBoolean()) {
				return new Vector3f(buf.readFloat());
			} else {
				return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
			}
		}

		@Override
		public void encode(ByteBuf buf, Vector3f v) {
			if (v.x == v.y && v.x == v.z) {
				buf.writeBoolean(true);
				buf.writeFloat(v.x);
			} else {
				buf.writeBoolean(false);
				buf.writeFloat(v.x);
				buf.writeFloat(v.y);
				buf.writeFloat(v.z);
			}
		}
	};

	StreamCodec<ByteBuf, Vector4f> VEC4S = new StreamCodec<>() {
		@Override
		public Vector4f decode(ByteBuf buf) {
			if (buf.readBoolean()) {
				var v = buf.readFloat();
				return new Vector4f(v, v, v, 1F);
			} else {
				return new Vector4f(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
			}
		}

		@Override
		public void encode(ByteBuf buf, Vector4f v) {
			if (v.x == v.y && v.x == v.z && v.w == 1F) {
				buf.writeBoolean(true);
				buf.writeFloat(v.x);
			} else {
				buf.writeBoolean(false);
				buf.writeFloat(v.x);
				buf.writeFloat(v.y);
				buf.writeFloat(v.z);
				buf.writeFloat(v.w);
			}
		}
	};

	StreamCodec<ByteBuf, Matrix2f> MAT2 = new StreamCodec<>() {
		@Override
		public Matrix2f decode(ByteBuf buf) {
			return new Matrix2f(
				buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat()
			);
		}

		@Override
		public void encode(ByteBuf buf, Matrix2f v) {
			buf.writeFloat(v.m00());
			buf.writeFloat(v.m01());
			buf.writeFloat(v.m10());
			buf.writeFloat(v.m11());
		}
	};

	StreamCodec<ByteBuf, Matrix3f> MAT3 = new StreamCodec<>() {
		@Override
		public Matrix3f decode(ByteBuf buf) {
			return new Matrix3f(
				buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(), buf.readFloat()
			);
		}

		@Override
		public void encode(ByteBuf buf, Matrix3f v) {
			buf.writeFloat(v.m00());
			buf.writeFloat(v.m01());
			buf.writeFloat(v.m02());
			buf.writeFloat(v.m10());
			buf.writeFloat(v.m11());
			buf.writeFloat(v.m12());
			buf.writeFloat(v.m20());
			buf.writeFloat(v.m21());
			buf.writeFloat(v.m22());
		}
	};

	StreamCodec<ByteBuf, Matrix4f> MAT4 = new StreamCodec<>() {
		@Override
		public Matrix4f decode(ByteBuf buf) {
			return new Matrix4f(
				buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat()
			);
		}

		@Override
		public void encode(ByteBuf buf, Matrix4f v) {
			buf.writeFloat(v.m00());
			buf.writeFloat(v.m01());
			buf.writeFloat(v.m02());
			buf.writeFloat(v.m03());
			buf.writeFloat(v.m10());
			buf.writeFloat(v.m11());
			buf.writeFloat(v.m12());
			buf.writeFloat(v.m13());
			buf.writeFloat(v.m20());
			buf.writeFloat(v.m21());
			buf.writeFloat(v.m22());
			buf.writeFloat(v.m23());
			buf.writeFloat(v.m30());
			buf.writeFloat(v.m31());
			buf.writeFloat(v.m32());
			buf.writeFloat(v.m33());
		}
	};

	StreamCodec<ByteBuf, Vector2d> DVEC2 = new StreamCodec<>() {
		@Override
		public Vector2d decode(ByteBuf buf) {
			return new Vector2d(buf.readDouble(), buf.readDouble());
		}

		@Override
		public void encode(ByteBuf buf, Vector2d v) {
			buf.writeDouble(v.x);
			buf.writeDouble(v.y);
		}
	};

	StreamCodec<ByteBuf, Vector3d> DVEC3 = new StreamCodec<>() {
		@Override
		public Vector3d decode(ByteBuf buf) {
			return new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		}

		@Override
		public void encode(ByteBuf buf, Vector3d v) {
			buf.writeDouble(v.x);
			buf.writeDouble(v.y);
			buf.writeDouble(v.z);
		}
	};

	StreamCodec<ByteBuf, Vector4d> DVEC4 = new StreamCodec<>() {
		@Override
		public Vector4d decode(ByteBuf buf) {
			return new Vector4d(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
		}

		@Override
		public void encode(ByteBuf buf, Vector4d v) {
			buf.writeDouble(v.x);
			buf.writeDouble(v.y);
			buf.writeDouble(v.z);
			buf.writeDouble(v.w);
		}
	};

	StreamCodec<ByteBuf, Quaterniond> DQUATERNION = new StreamCodec<>() {
		@Override
		public Quaterniond decode(ByteBuf buf) {
			return new Quaterniond(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
		}

		@Override
		public void encode(ByteBuf buf, Quaterniond v) {
			buf.writeDouble(v.x);
			buf.writeDouble(v.y);
			buf.writeDouble(v.z);
			buf.writeDouble(v.w);
		}
	};

	StreamCodec<ByteBuf, Vector2d> DVEC2S = new StreamCodec<>() {
		@Override
		public Vector2d decode(ByteBuf buf) {
			if (buf.readBoolean()) {
				return new Vector2d(buf.readDouble());
			} else {
				return new Vector2d(buf.readDouble(), buf.readDouble());
			}
		}

		@Override
		public void encode(ByteBuf buf, Vector2d v) {
			if (v.x == v.y) {
				buf.writeBoolean(true);
				buf.writeDouble(v.x);
			} else {
				buf.writeBoolean(false);
				buf.writeDouble(v.x);
				buf.writeDouble(v.y);
			}
		}
	};

	StreamCodec<ByteBuf, Vector3d> DVEC3S = new StreamCodec<>() {
		@Override
		public Vector3d decode(ByteBuf buf) {
			if (buf.readBoolean()) {
				return new Vector3d(buf.readDouble());
			} else {
				return new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
			}
		}

		@Override
		public void encode(ByteBuf buf, Vector3d v) {
			if (v.x == v.y && v.x == v.z) {
				buf.writeBoolean(true);
				buf.writeDouble(v.x);
			} else {
				buf.writeBoolean(false);
				buf.writeDouble(v.x);
				buf.writeDouble(v.y);
				buf.writeDouble(v.z);
			}
		}
	};

	StreamCodec<ByteBuf, Vector4d> DVEC4S = new StreamCodec<>() {
		@Override
		public Vector4d decode(ByteBuf buf) {
			if (buf.readBoolean()) {
				var v = buf.readFloat();
				return new Vector4d(v, v, v, 1D);
			} else {
				return new Vector4d(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
			}
		}

		@Override
		public void encode(ByteBuf buf, Vector4d v) {
			if (v.x == v.y && v.x == v.z && v.w == 1D) {
				buf.writeBoolean(true);
				buf.writeDouble(v.x);
			} else {
				buf.writeBoolean(false);
				buf.writeDouble(v.x);
				buf.writeDouble(v.y);
				buf.writeDouble(v.z);
				buf.writeDouble(v.w);
			}
		}
	};

	StreamCodec<ByteBuf, Matrix2d> DMAT2 = new StreamCodec<>() {
		@Override
		public Matrix2d decode(ByteBuf buf) {
			return new Matrix2d(
				buf.readDouble(), buf.readDouble(),
				buf.readDouble(), buf.readDouble()
			);
		}

		@Override
		public void encode(ByteBuf buf, Matrix2d v) {
			buf.writeDouble(v.m00());
			buf.writeDouble(v.m01());
			buf.writeDouble(v.m10());
			buf.writeDouble(v.m11());
		}
	};

	StreamCodec<ByteBuf, Matrix2d> FDMAT2 = MAT2.map(Matrix2d::new, m -> new Matrix2f(
		(float) m.m00(), (float) m.m01(),
		(float) m.m10(), (float) m.m11()
	));

	StreamCodec<ByteBuf, Matrix3d> DMAT3 = new StreamCodec<>() {
		@Override
		public Matrix3d decode(ByteBuf buf) {
			return new Matrix3d(
				buf.readDouble(), buf.readDouble(), buf.readDouble(),
				buf.readDouble(), buf.readDouble(), buf.readDouble(),
				buf.readDouble(), buf.readDouble(), buf.readDouble()
			);
		}

		@Override
		public void encode(ByteBuf buf, Matrix3d v) {
			buf.writeDouble(v.m00());
			buf.writeDouble(v.m01());
			buf.writeDouble(v.m02());
			buf.writeDouble(v.m10());
			buf.writeDouble(v.m11());
			buf.writeDouble(v.m12());
			buf.writeDouble(v.m20());
			buf.writeDouble(v.m21());
			buf.writeDouble(v.m22());
		}
	};

	StreamCodec<ByteBuf, Matrix3d> FDMAT3 = MAT3.map(Matrix3d::new, m -> new Matrix3f(
		(float) m.m00(), (float) m.m01(), (float) m.m02(),
		(float) m.m10(), (float) m.m11(), (float) m.m12(),
		(float) m.m20(), (float) m.m21(), (float) m.m22()
	));

	StreamCodec<ByteBuf, Matrix4d> DMAT4 = new StreamCodec<>() {
		@Override
		public Matrix4d decode(ByteBuf buf) {
			return new Matrix4d(
				buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(),
				buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(),
				buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(),
				buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble()
			);
		}

		@Override
		public void encode(ByteBuf buf, Matrix4d v) {
			buf.writeDouble(v.m00());
			buf.writeDouble(v.m01());
			buf.writeDouble(v.m02());
			buf.writeDouble(v.m03());
			buf.writeDouble(v.m10());
			buf.writeDouble(v.m11());
			buf.writeDouble(v.m12());
			buf.writeDouble(v.m13());
			buf.writeDouble(v.m20());
			buf.writeDouble(v.m21());
			buf.writeDouble(v.m22());
			buf.writeDouble(v.m23());
			buf.writeDouble(v.m30());
			buf.writeDouble(v.m31());
			buf.writeDouble(v.m32());
			buf.writeDouble(v.m33());
		}
	};

	StreamCodec<ByteBuf, Matrix4d> FDMAT4 = MAT4.map(Matrix4d::new, m -> new Matrix4f(
		(float) m.m00(), (float) m.m01(), (float) m.m02(), (float) m.m03(),
		(float) m.m10(), (float) m.m11(), (float) m.m12(), (float) m.m13(),
		(float) m.m20(), (float) m.m21(), (float) m.m22(), (float) m.m23(),
		(float) m.m30(), (float) m.m31(), (float) m.m32(), (float) m.m33()
	));

	StreamCodec<ByteBuf, Vector2i> IVEC2 = new StreamCodec<>() {
		@Override
		public Vector2i decode(ByteBuf buf) {
			return new Vector2i(buf.readInt(), buf.readInt());
		}

		@Override
		public void encode(ByteBuf buf, Vector2i v) {
			buf.writeInt(v.x());
			buf.writeInt(v.y());
		}
	};

	StreamCodec<ByteBuf, Vector3i> IVEC3 = new StreamCodec<>() {
		@Override
		public Vector3i decode(ByteBuf buf) {
			return new Vector3i(buf.readInt(), buf.readInt(), buf.readInt());
		}

		@Override
		public void encode(ByteBuf buf, Vector3i v) {
			buf.writeInt(v.x());
			buf.writeInt(v.y());
			buf.writeInt(v.z());
		}
	};

	StreamCodec<ByteBuf, Vector4i> IVEC4 = new StreamCodec<>() {
		@Override
		public Vector4i decode(ByteBuf buf) {
			return new Vector4i(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
		}

		@Override
		public void encode(ByteBuf buf, Vector4i v) {
			buf.writeInt(v.x());
			buf.writeInt(v.y());
			buf.writeInt(v.z());
		}
	};

	StreamCodec<ByteBuf, Vector2i> VIVEC2 = new StreamCodec<>() {
		@Override
		public Vector2i decode(ByteBuf buf) {
			return new Vector2i(VarInt.read(buf), VarInt.read(buf));
		}

		@Override
		public void encode(ByteBuf buf, Vector2i v) {
			VarInt.write(buf, v.x());
			VarInt.write(buf, v.y());
		}
	};

	StreamCodec<ByteBuf, Vector3i> VIVEC3 = new StreamCodec<>() {
		@Override
		public Vector3i decode(ByteBuf buf) {
			return new Vector3i(VarInt.read(buf), VarInt.read(buf), VarInt.read(buf));
		}

		@Override
		public void encode(ByteBuf buf, Vector3i v) {
			VarInt.write(buf, v.x());
			VarInt.write(buf, v.y());
			VarInt.write(buf, v.z());
		}
	};

	StreamCodec<ByteBuf, Vector4i> VIVEC4 = new StreamCodec<>() {
		@Override
		public Vector4i decode(ByteBuf buf) {
			return new Vector4i(VarInt.read(buf), VarInt.read(buf), VarInt.read(buf), VarInt.read(buf));
		}

		@Override
		public void encode(ByteBuf buf, Vector4i v) {
			VarInt.write(buf, v.x());
			VarInt.write(buf, v.y());
			VarInt.write(buf, v.z());
			VarInt.write(buf, v.w());
		}
	};

	StreamCodec<ByteBuf, Vector2fc> VEC2C = VEC2.map(Function.identity(), v -> v instanceof Vector2f c ? c : new Vector2f(v));
	StreamCodec<ByteBuf, Vector3fc> VEC3C = VEC3.map(Function.identity(), v -> v instanceof Vector3f c ? c : new Vector3f(v));
	StreamCodec<ByteBuf, Vector4fc> VEC4C = VEC4.map(Function.identity(), v -> v instanceof Vector4f c ? c : new Vector4f(v));
	StreamCodec<ByteBuf, Vector2fc> VEC2SC = VEC2S.map(Function.identity(), v -> v instanceof Vector2f c ? c : new Vector2f(v));
	StreamCodec<ByteBuf, Vector3fc> VEC3SC = VEC3S.map(Function.identity(), v -> v instanceof Vector3f c ? c : new Vector3f(v));
	StreamCodec<ByteBuf, Vector4fc> VEC4SC = VEC4S.map(Function.identity(), v -> v instanceof Vector4f c ? c : new Vector4f(v));
	StreamCodec<ByteBuf, Quaternionfc> QUATERNIONC = QUATERNION.map(Function.identity(), v -> v instanceof Quaternionf c ? c : new Quaternionf(v));
	StreamCodec<ByteBuf, Matrix2fc> MAT2C = MAT2.map(Function.identity(), v -> v instanceof Matrix2f c ? c : new Matrix2f(v));
	StreamCodec<ByteBuf, Matrix3fc> MAT3C = MAT3.map(Function.identity(), v -> v instanceof Matrix3f c ? c : new Matrix3f(v));
	StreamCodec<ByteBuf, Matrix4fc> MAT4C = MAT4.map(Function.identity(), v -> v instanceof Matrix4f c ? c : new Matrix4f(v));
	StreamCodec<ByteBuf, Vector2dc> DVEC2C = DVEC2.map(Function.identity(), v -> v instanceof Vector2d c ? c : new Vector2d(v));
	StreamCodec<ByteBuf, Vector3dc> DVEC3C = DVEC3.map(Function.identity(), v -> v instanceof Vector3d c ? c : new Vector3d(v));
	StreamCodec<ByteBuf, Vector4dc> DVEC4C = DVEC4.map(Function.identity(), v -> v instanceof Vector4d c ? c : new Vector4d(v));
	StreamCodec<ByteBuf, Vector2dc> DVEC2SC = DVEC2S.map(Function.identity(), v -> v instanceof Vector2d c ? c : new Vector2d(v));
	StreamCodec<ByteBuf, Vector3dc> DVEC3SC = DVEC3S.map(Function.identity(), v -> v instanceof Vector3d c ? c : new Vector3d(v));
	StreamCodec<ByteBuf, Vector4dc> DVEC4SC = DVEC4S.map(Function.identity(), v -> v instanceof Vector4d c ? c : new Vector4d(v));
	StreamCodec<ByteBuf, Quaterniondc> DQUATERNIONC = DQUATERNION.map(Function.identity(), v -> v instanceof Quaterniond c ? c : new Quaterniond(v));
	StreamCodec<ByteBuf, Matrix2dc> DMAT2C = DMAT2.map(Function.identity(), v -> v instanceof Matrix2d c ? c : new Matrix2d(v));
	StreamCodec<ByteBuf, Matrix3dc> DMAT3C = DMAT3.map(Function.identity(), v -> v instanceof Matrix3d c ? c : new Matrix3d(v));
	StreamCodec<ByteBuf, Matrix4dc> DMAT4C = DMAT4.map(Function.identity(), v -> v instanceof Matrix4d c ? c : new Matrix4d(v));
	StreamCodec<ByteBuf, Matrix2dc> FDMAT2C = FDMAT2.map(Function.identity(), v -> v instanceof Matrix2d c ? c : new Matrix2d(v));
	StreamCodec<ByteBuf, Matrix3dc> FDMAT3C = FDMAT3.map(Function.identity(), v -> v instanceof Matrix3d c ? c : new Matrix3d(v));
	StreamCodec<ByteBuf, Matrix4dc> FDMAT4C = FDMAT4.map(Function.identity(), v -> v instanceof Matrix4d c ? c : new Matrix4d(v));
	StreamCodec<ByteBuf, Vector2ic> IVEC2C = IVEC2.map(Function.identity(), v -> v instanceof Vector2i c ? c : new Vector2i(v));
	StreamCodec<ByteBuf, Vector3ic> IVEC3C = IVEC3.map(Function.identity(), v -> v instanceof Vector3i c ? c : new Vector3i(v));
	StreamCodec<ByteBuf, Vector4ic> IVEC4C = IVEC4.map(Function.identity(), v -> v instanceof Vector4i c ? c : new Vector4i(v));
	StreamCodec<ByteBuf, Vector2ic> VIVEC2C = VIVEC2.map(Function.identity(), v -> v instanceof Vector2i c ? c : new Vector2i(v));
	StreamCodec<ByteBuf, Vector3ic> VIVEC3C = VIVEC3.map(Function.identity(), v -> v instanceof Vector3i c ? c : new Vector3i(v));
	StreamCodec<ByteBuf, Vector4ic> VIVEC4C = VIVEC4.map(Function.identity(), v -> v instanceof Vector4i c ? c : new Vector4i(v));
}
