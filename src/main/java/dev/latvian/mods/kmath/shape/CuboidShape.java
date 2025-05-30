package dev.latvian.mods.kmath.shape;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kmath.Rotation;
import dev.latvian.mods.kmath.Vec3f;
import dev.latvian.mods.kmath.codec.KMathStreamCodecs;
import dev.latvian.mods.kmath.render.BoxBuilder;
import dev.latvian.mods.kmath.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public record CuboidShape(Vec3f size, Rotation rotation) implements Shape {
	public static final MapCodec<CuboidShape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3f.CODEC.fieldOf("size").forGetter(CuboidShape::size),
		Rotation.CODEC.optionalFieldOf("rotation", Rotation.NONE).forGetter(CuboidShape::rotation)
	).apply(instance, CuboidShape::new));

	public static final StreamCodec<ByteBuf, CuboidShape> STREAM_CODEC = StreamCodec.composite(
		Vec3f.STREAM_CODEC, CuboidShape::size,
		KMathStreamCodecs.optional(Rotation.STREAM_CODEC, Rotation.NONE), CuboidShape::rotation,
		CuboidShape::new
	);

	@Override
	public ShapeType type() {
		return ShapeType.CUBOID;
	}

	@Override
	public Shape optimize() {
		if (size.x() <= 0F && size.y() <= 0F && size.z() <= 0F) {
			return EmptyShape.INSTANCE;
		} else if (size.x() == size.y() && size.x() == size.z() && rotation.isNone()) {
			return new CubeShape(size.x());
		}

		return this;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
		float sx = size.x() / 2F;
		float sy = size.y() / 2F;
		float sz = size.z() / 2F;

		if (!rotation.isNone()) {
			callback = callback.withTransformedPositionsAndNormals(rotation.rotateYXZ(new Matrix4f()), rotation.rotateYXZ(new Matrix3f()), true);
		}

		BoxBuilder.lines(x - sx, y - sy, z - sz, x + sx, y + sy, z + sz, callback);
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		float sx = size.x() / 2F;
		float sy = size.y() / 2F;
		float sz = size.z() / 2F;

		if (!rotation.isNone()) {
			callback = callback.withTransformedPositionsAndNormals(rotation.rotateYXZ(new Matrix4f()), rotation.rotateYXZ(new Matrix3f()), true);
		}

		BoxBuilder.quads(x - sx, y - sy, z - sz, x + sx, y + sy, z + sz, callback);
	}
}
