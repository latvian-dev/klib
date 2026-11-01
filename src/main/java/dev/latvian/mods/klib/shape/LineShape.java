package dev.latvian.mods.klib.shape;

import dev.latvian.mods.klib.codec.JOMLCodecs;
import dev.latvian.mods.klib.codec.JOMLStreamCodecs;
import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.math.Vec3f;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3fc;

public record LineShape(Vector3fc vector, AAFBB box) implements Shape {
	public static final UnitType<ByteBuf, Shape> UNIT_DOWN_LINE = UnitType.create("unit_down_line", new LineShape(Vec3f.DOWN));
	public static final UnitType<ByteBuf, Shape> UNIT_UP_LINE = UnitType.create("unit_up_line", new LineShape(Vec3f.UP));
	public static final UnitType<ByteBuf, Shape> UNIT_NORTH_LINE = UnitType.create("unit_north_line", new LineShape(Vec3f.NORTH));
	public static final UnitType<ByteBuf, Shape> UNIT_SOUTH_LINE = UnitType.create("unit_south_line", new LineShape(Vec3f.SOUTH));
	public static final UnitType<ByteBuf, Shape> UNIT_WEST_LINE = UnitType.create("unit_west_line", new LineShape(Vec3f.WEST));
	public static final UnitType<ByteBuf, Shape> UNIT_EAST_LINE = UnitType.create("unit_east_line", new LineShape(Vec3f.EAST));

	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"line",
		"vector",
		JOMLCodecs.VEC3C,
		JOMLStreamCodecs.VEC3C,
		LineShape::new,
		LineShape::vector
	);

	public LineShape(Vector3fc vector) {
		this(vector, AAFBB.between(Vec3f.ZERO, vector));
	}

	@Override
	public DynamicType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public Ref<Shape> optimize(Ref<Shape> self) {
		if (vector.lengthSquared() <= 0F) {
			return EMPTY;
		} else if (vector == Vec3f.DOWN) {
			return UNIT_DOWN_LINE;
		} else if (vector == Vec3f.UP) {
			return UNIT_UP_LINE;
		} else if (vector == Vec3f.NORTH) {
			return UNIT_NORTH_LINE;
		} else if (vector == Vec3f.SOUTH) {
			return UNIT_SOUTH_LINE;
		} else if (vector == Vec3f.WEST) {
			return UNIT_WEST_LINE;
		} else if (vector == Vec3f.EAST) {
			return UNIT_EAST_LINE;
		} else {
			return self;
		}
	}

	@Override
	public void buildLines(LineVertexCallback callback) {
		callback.line(0F, 0F, 0F, vector.x(), vector.y(), vector.z());
	}

	@Override
	public void buildOuterLines(LineVertexCallback callback) {
		callback.cuboidLines(box);
	}

	@Override
	public void buildQuads(QuadVertexCallback callback) {
	}

	@Override
	public boolean contains(float x, float y, float z) {
		float len = vector.lengthSquared();

		if (len <= 0F) {
			return Math.abs(x) <= 0.00001F && Math.abs(y) <= 0.00001F && Math.abs(z) <= 0.00001F;
		}

		var dx = x - vector.x();
		var dy = y - vector.y();
		var dz = z - vector.z();
		var d = dx * vector.x() + dy * vector.y() + dz * vector.z();
		return d >= 0F && d <= len && dx * dx + dy * dy + dz * dz - d * d / len <= 0.00001F;
	}
}
