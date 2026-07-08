package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import org.joml.Vector3fc;

public record CubeShape(float size) implements Shape {
	public static final UnitType<ByteBuf, Shape> UNIT_CUBE = UnitType.create("unit_cube", new CubeShape(1F));

	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"cube",
		"size",
		Codec.FLOAT,
		ByteBufCodecs.FLOAT,
		CubeShape::new,
		CubeShape::size
	);

	@Override
	public CustomRegistryType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
		float r = size / 2F;
		CuboidBuilder.lines(x - r, y - r, z - r, x + r, y + r, z + r, callback);
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		float r = size / 2F;
		CuboidBuilder.quads(x - r, y - r, z - r, x + r, y + r, z + r, callback);
	}

	@Override
	public boolean contains(Vector3fc p) {
		var r = size / 2F;
		return p.x() >= -r && p.x() <= r && p.y() >= -r && p.y() <= r && p.z() >= -r && p.z() <= r;
	}

	@Override
	public boolean isVisible(double x, double y, double z, FrustumCheck frustum) {
		double r = size / 2D;
		return frustum.isVisible(x - r, y - r, z - r, x + r, y + r, z + r);
	}

	@Override
	public Shape optimize() {
		return size == 1F ? UNIT_CUBE.value() : this;
	}
}
