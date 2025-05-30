package dev.latvian.mods.kmath.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kmath.codec.KMathStreamCodecs;
import dev.latvian.mods.kmath.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CylinderShape(float radius, float height) implements Shape {
	public static final MapCodec<CylinderShape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("radius").forGetter(CylinderShape::radius),
		Codec.FLOAT.optionalFieldOf("height", 0F).forGetter(CylinderShape::radius)
	).apply(instance, CylinderShape::new));

	public static final StreamCodec<ByteBuf, CylinderShape> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, CylinderShape::radius,
		KMathStreamCodecs.FLOAT_OR_ZERO, CylinderShape::height,
		CylinderShape::new
	);

	@Override
	public ShapeType type() {
		return ShapeType.CYLINDER;
	}

	@Override
	public Shape optimize() {
		if (radius <= 0F && height <= 0F) {
			return EmptyShape.INSTANCE;
		}

		return this;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
		float r = Math.max(radius, 0F);
		float h = Math.max(height, 0F) / 2F;
		double rs = Math.PI * 2D / 24D;

		for (int i = 0; i < 24; i++) {
			float cx = (float) (Math.cos(i * rs) * r);
			float cz = (float) (Math.sin(i * rs) * r);
			float nx = (float) (Math.cos((i + 1D) * rs) * r);
			float nz = (float) (Math.sin((i + 1D) * rs) * r);

			// FIXME (0F, -1F, 0F)

			callback.acceptPos(x, y - h, z).acceptNormal(0F, -1F, 0F);
			callback.acceptPos(x + cx, y - h, z + cz).acceptNormal(0F, -1F, 0F);

			callback.acceptPos(x + cx, y - h, z + cz).acceptNormal(0F, -1F, 0F);
			callback.acceptPos(x + nx, y - h, z + nz).acceptNormal(0F, -1F, 0F);

			if (h > 0F) {
				callback.acceptPos(x, y + h, z).acceptNormal(0F, -1F, 0F);
				callback.acceptPos(x + cx, y + h, z + cz).acceptNormal(0F, -1F, 0F);

				callback.acceptPos(x + cx, y + h, z + cz).acceptNormal(0F, -1F, 0F);
				callback.acceptPos(x + nx, y + h, z + nz).acceptNormal(0F, -1F, 0F);

				callback.acceptPos(x + cx, y - h, z + cz).acceptNormal(0F, 1F, 0F);
				callback.acceptPos(x + cx, y + h, z + cz).acceptNormal(0F, 1F, 0F);
			}
		}
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		// FIXME
	}
}
