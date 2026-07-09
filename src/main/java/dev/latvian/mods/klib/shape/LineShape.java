package dev.latvian.mods.klib.shape;

import dev.latvian.mods.klib.math.Vec3f;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3fc;

public record LineShape(Vec3f vector) implements Shape {
	public static final UnitType<ByteBuf, Shape> UNIT_DOWN_LINE = UnitType.create("unit_down_line", new LineShape(Vec3f.DOWN));
	public static final UnitType<ByteBuf, Shape> UNIT_UP_LINE = UnitType.create("unit_up_line", new LineShape(Vec3f.UP));
	public static final UnitType<ByteBuf, Shape> UNIT_NORTH_LINE = UnitType.create("unit_north_line", new LineShape(Vec3f.NORTH));
	public static final UnitType<ByteBuf, Shape> UNIT_SOUTH_LINE = UnitType.create("unit_south_line", new LineShape(Vec3f.SOUTH));
	public static final UnitType<ByteBuf, Shape> UNIT_WEST_LINE = UnitType.create("unit_west_line", new LineShape(Vec3f.WEST));
	public static final UnitType<ByteBuf, Shape> UNIT_EAST_LINE = UnitType.create("unit_east_line", new LineShape(Vec3f.EAST));

	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"line",
		"vector",
		Vec3f.CODEC,
		Vec3f.STREAM_CODEC,
		LineShape::new,
		LineShape::vector
	);

	@Override
	public DynamicType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public Shape optimize() {
		if (vector.lengthSq() <= 0F) {
			return EmptyShape.INSTANCE;
		} else if (vector == Vec3f.DOWN) {
			return UNIT_DOWN_LINE.value();
		} else if (vector == Vec3f.UP) {
			return UNIT_UP_LINE.value();
		} else if (vector == Vec3f.NORTH) {
			return UNIT_NORTH_LINE.value();
		} else if (vector == Vec3f.SOUTH) {
			return UNIT_SOUTH_LINE.value();
		} else if (vector == Vec3f.WEST) {
			return UNIT_WEST_LINE.value();
		} else if (vector == Vec3f.EAST) {
			return UNIT_EAST_LINE.value();
		} else {
			return this;
		}
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
		callback.line(x, y, z, x + vector.x(), y + vector.y(), z + vector.z());
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
	}

	@Override
	public boolean contains(Vector3fc p) {
		// check if point is inside vector
		float len = vector.lengthSq();

		if (len <= 0F) {
			return p.lengthSquared() <= 0F;
		}

		var dx = p.x() - vector.x();
		var dy = p.y() - vector.y();
		var dz = p.z() - vector.z();
		var d = dx * vector.x() + dy * vector.y() + dz * vector.z();
		return d >= 0F && d <= len && dx * dx + dy * dy + dz * dz - d * d / len <= 0.0001F;
	}
}
