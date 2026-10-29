package dev.latvian.mods.klib.math;

import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Matrix3fc;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record ComputedTransformation(
	Transformation transformation,
	boolean identity,
	Matrix4fc matrix,
	Matrix4fc inverseMatrix,
	Matrix3fc normalMatrix,
	Vector3fc origin,
	Vector3fc corner
) {
	public static final ComputedTransformation IDENTITY = new ComputedTransformation(Transformation.IDENTITY, true);
	public static final Codec<ComputedTransformation> CODEC = Transformation.EXTENDED_CODEC.xmap(ComputedTransformation::of, ComputedTransformation::transformation);
	public static final StreamCodec<ByteBuf, ComputedTransformation> STREAM_CODEC = MCStreamCodecs.TRANSFORMATION.map(ComputedTransformation::of, ComputedTransformation::transformation);
	public static final DataType<ComputedTransformation> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC);

	public static ComputedTransformation of(Transformation transformation) {
		return transformation.equals(Transformation.IDENTITY) ? IDENTITY : new ComputedTransformation(transformation, false);
	}

	private ComputedTransformation(Transformation transformation, boolean identity) {
		this(
			transformation,
			identity,
			transformation.getMatrix(),
			transformation.getMatrixCopy().invert(),
			transformation.getNormalMatrix(),
			new Vector3f(0F, 0F, 0F).mulPosition(transformation.getMatrix()),
			new Vector3f(1F, 1F, 1F).mulPosition(transformation.getMatrix())
		);
	}

	public VertexCallback transform(VertexCallback callback) {
		return callback.withTransformedPositionsAndNormals(matrix, normalMatrix, true);
	}

	public LineVertexCallback transformLines(LineVertexCallback callback) {
		var vec = new Vector3f();
		var normal = new Vector3f();

		return (x, y, z, nx, ny, nz) -> {
			vec.set(x, y, z);
			vec.mulPosition(matrix);
			normal.set(nx, ny, nz);
			normal.mul(normalMatrix);
			normal.normalize();
			callback.lineVertex(vec.x, vec.y, vec.z, normal.x, normal.y, normal.z);
		};
	}

	public QuadVertexCallback transformQuads(QuadVertexCallback callback) {
		var vec = new Vector3f();
		var normal = new Vector3f();

		return (x, y, z, u, v, nx, ny, nz) -> {
			vec.set(x, y, z);
			vec.mulPosition(matrix);
			normal.set(nx, ny, nz);
			normal.mul(normalMatrix);
			normal.normalize();
			callback.quadVertex(vec.x, vec.y, vec.z, u, v, normal.x, normal.y, normal.z);
		};
	}
}
