package dev.latvian.mods.klib.shape;

import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.math.Vec3f;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3fc;

public record CuboidShape(Vec3f size) implements Shape {
	public static final UnitType<ByteBuf, Shape> UNIT_SQUARE = UnitType.create("unit_square", new CuboidShape(new Vec3f(1F, 0F, 1F)));

	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"cuboid",
		"size",
		Vec3f.CODEC,
		Vec3f.STREAM_CODEC,
		CuboidShape::new,
		CuboidShape::size
	);

	@Override
	public DynamicType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public Ref<Shape> optimize(Ref<Shape> self) {
		if (size.x() <= 0F && size.y() <= 0F && size.z() <= 0F) {
			return EMPTY;
		} else if (size.x() == 1F && size.y() == 1F && size.z() == 1F) {
			return CubeShape.UNIT_CUBE;
		} else if (size.x() == size.y() && size.x() == size.z()) {
			return new CubeShape(size.x()).ref();
		} else if (size.x() == 1F && size.y() == 0F && size.z() == 1F) {
			return UNIT_SQUARE;
		}

		return self;
	}

	@Override
	public void buildLines(LineVertexCallback callback) {
		float sx = size.x() / 2F;
		float sy = size.y() / 2F;
		float sz = size.z() / 2F;
		callback.cuboidLines(-sx, -sy, -sz, sx, sy, sz);
	}

	@Override
	public void buildQuads(QuadVertexCallback callback) {
		float sx = size.x() / 2F;
		float sy = size.y() / 2F;
		float sz = size.z() / 2F;
		callback.cuboidQuads(-sx, -sy, -sz, sx, sy, sz);
	}

	@Override
	public boolean contains(Vector3fc p) {
		float sx = size.x() / 2F;
		float sy = size.y() / 2F;
		float sz = size.z() / 2F;
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
