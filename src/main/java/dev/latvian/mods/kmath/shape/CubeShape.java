package dev.latvian.mods.kmath.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kmath.FrustumCheck;
import dev.latvian.mods.kmath.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3fc;

public record CubeShape(float size) implements Shape {
	public static final CubeShape UNIT = new CubeShape(1F);

	public static final MapCodec<CubeShape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("size").forGetter(CubeShape::size)
	).apply(instance, CubeShape::new));

	public static final StreamCodec<ByteBuf, CubeShape> STREAM_CODEC = ByteBufCodecs.FLOAT.map(CubeShape::new, CubeShape::size);
	public static final ShapeType TYPE = new ShapeType("cube", CODEC, STREAM_CODEC);

	@Override
	public ShapeType type() {
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
}
