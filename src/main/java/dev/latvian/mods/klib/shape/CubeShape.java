package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.util.ID;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import org.joml.Vector3fc;

public record CubeShape(float size) implements Shape {
	public static final CustomRegistryType.Unit<ByteBuf, Shape> UNIT_CUBE = Shape.REGISTRY.unit(ID.klib("unit_cube"), new CubeShape(1F));

	public static final CustomRegistryType<ByteBuf, Shape> TYPE = Shape.REGISTRY.dynamic(ID.klib("cube"),
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.FLOAT.fieldOf("size").forGetter(CubeShape::size)
		).apply(instance, CubeShape::new)),
		CompositeStreamCodec.of(
			ByteBufCodecs.FLOAT, CubeShape::size,
			CubeShape::new
		)
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
