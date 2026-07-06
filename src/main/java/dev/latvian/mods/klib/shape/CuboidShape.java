package dev.latvian.mods.klib.shape;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.math.Vec3f;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3fc;

public record CuboidShape(Vec3f size) implements Shape {
	public static final UnitType<ByteBuf, Shape> UNIT_SQUARE = UnitType.create("unit_square", new CuboidShape(new Vec3f(1F, 0F, 1F)));

	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"cuboid",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Vec3f.CODEC.fieldOf("size").forGetter(CuboidShape::size)
		).apply(instance, CuboidShape::new)),
		CompositeStreamCodec.of(
			Vec3f.STREAM_CODEC, CuboidShape::size,
			CuboidShape::new
		)
	);

	@Override
	public CustomRegistryType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public Shape optimize() {
		if (size.x() <= 0F && size.y() <= 0F && size.z() <= 0F) {
			return EmptyShape.INSTANCE;
		} else if (size.x() == size.y() && size.x() == size.z()) {
			return new CubeShape(size.x()).optimize();
		} else if (size.x() == 1F && size.y() == 0F && size.z() == 1F) {
			return UNIT_SQUARE.value();
		}

		return this;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
		float sx = size.x() / 2F;
		float sy = size.y() / 2F;
		float sz = size.z() / 2F;
		CuboidBuilder.lines(x - sx, y - sy, z - sz, x + sx, y + sy, z + sz, callback);
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		float sx = size.x() / 2F;
		float sy = size.y() / 2F;
		float sz = size.z() / 2F;
		CuboidBuilder.quads(x - sx, y - sy, z - sz, x + sx, y + sy, z + sz, callback);
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
