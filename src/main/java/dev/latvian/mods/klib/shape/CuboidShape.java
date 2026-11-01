package dev.latvian.mods.klib.shape;

import dev.latvian.mods.klib.codec.JOMLCodecs;
import dev.latvian.mods.klib.codec.JOMLStreamCodecs;
import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.math.Vec3f;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3fc;

public record CuboidShape(Vector3fc size, AAFBB box) implements Shape {
	public static final UnitType<ByteBuf, Shape> UNIT_SQUARE = UnitType.create("unit_square", new CuboidShape(Vec3f.of(1F, 0F, 1F)));

	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"cuboid",
		"size",
		JOMLCodecs.VEC3C,
		JOMLStreamCodecs.VEC3C,
		CuboidShape::new,
		CuboidShape::size
	);

	public CuboidShape(Vector3fc size) {
		this(size, AAFBB.centered(size.x(), size.y(), size.z()));
	}

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
}
