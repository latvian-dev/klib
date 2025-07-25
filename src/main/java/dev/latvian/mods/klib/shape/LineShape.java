package dev.latvian.mods.klib.shape;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.math.Vec3f;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3fc;

public record LineShape(Vec3f vector) implements Shape {
	public static final LineShape DOWN_UNIT = new LineShape(Vec3f.DOWN);
	public static final LineShape UP_UNIT = new LineShape(Vec3f.UP);
	public static final LineShape NORTH_UNIT = new LineShape(Vec3f.NORTH);
	public static final LineShape SOUTH_UNIT = new LineShape(Vec3f.SOUTH);
	public static final LineShape WEST_UNIT = new LineShape(Vec3f.WEST);
	public static final LineShape EAST_UNIT = new LineShape(Vec3f.EAST);

	public static final MapCodec<LineShape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3f.CODEC.fieldOf("vector").forGetter(LineShape::vector)
	).apply(instance, LineShape::new));

	public static final StreamCodec<ByteBuf, LineShape> STREAM_CODEC = StreamCodec.composite(
		Vec3f.STREAM_CODEC, LineShape::vector,
		LineShape::new
	);

	public static final ShapeType TYPE = new ShapeType("line", CODEC, STREAM_CODEC);

	@Override
	public ShapeType type() {
		return TYPE;
	}

	@Override
	public Shape optimize() {
		if (vector.lengthSq() <= 0F) {
			return EmptyShape.INSTANCE;
		}

		return this;
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
