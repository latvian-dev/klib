package dev.latvian.mods.klib.shape;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.math.Rotation;
import dev.latvian.mods.klib.math.Vec3f;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record CuboidShape(Vec3f size, Rotation rotation) implements Shape {
	public static final CuboidShape SQUARE_UNIT = new CuboidShape(new Vec3f(1F, 0F, 1F), Rotation.NONE);

	public static final MapCodec<CuboidShape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3f.CODEC.fieldOf("size").forGetter(CuboidShape::size),
		Rotation.CODEC.optionalFieldOf("rotation", Rotation.NONE).forGetter(CuboidShape::rotation)
	).apply(instance, CuboidShape::new));

	public static final StreamCodec<ByteBuf, CuboidShape> STREAM_CODEC = CompositeStreamCodec.of(
		Vec3f.STREAM_CODEC, CuboidShape::size,
		KLibStreamCodecs.optional(Rotation.STREAM_CODEC, Rotation.NONE), CuboidShape::rotation,
		CuboidShape::new
	);

	public static final ShapeType TYPE = new ShapeType("cuboid", CODEC, STREAM_CODEC);

	@Override
	public ShapeType type() {
		return TYPE;
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

		CuboidBuilder.lines(x - sx, y - sy, z - sz, x + sx, y + sy, z + sz, callback);
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		float sx = size.x() / 2F;
		float sy = size.y() / 2F;
		float sz = size.z() / 2F;

		if (!rotation.isNone()) {
			var mat4 = new Matrix4f();
			var mat3 = new Matrix3f();
			mat4.translate(x, y, z);
			rotation.rotateYXZ(mat4);
			rotation.rotateYXZ(mat3);
			callback = callback.withTransformedPositionsAndNormals(mat4, mat3, true);
			CuboidBuilder.quads(-sx, -sy, -sz, sx, sy, sz, callback);
		} else {
			CuboidBuilder.quads(x - sx, y - sy, z - sz, x + sx, y + sy, z + sz, callback);
		}
	}

	@Override
	public boolean contains(Vector3fc p) {
		float sx = size.x() / 2F;
		float sy = size.y() / 2F;
		float sz = size.z() / 2F;

		if (!rotation.isNone()) {
			var mat = new Matrix3f();
			rotation.rotateYXZ(mat);
			var vec = new Vector3f(p.x() - sx, p.y() - sy, p.z() - sz).mul(mat);
			return vec.x() >= -sx && vec.x() <= sx && vec.y() >= -sy && vec.y() <= sy && vec.z() >= -sz && vec.z() <= sz;
		}

		return p.x() >= -sx && p.x() <= sx && p.y() >= -sy && p.y() <= sy && p.z() >= -sz && p.z() <= sz;
	}

	@Override
	public boolean isVisible(double x, double y, double z, FrustumCheck frustum) {
		double sx = size.x() / 2D;
		double sy = size.y() / 2D;
		double sz = size.z() / 2D;
		return frustum.isVisible(x - sx, y - sy, z - sz, x + sx, y + sy, z + sz);
	}
}
