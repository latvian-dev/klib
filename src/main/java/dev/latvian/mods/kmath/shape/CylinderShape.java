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
	public static final CylinderShape UNIT = new CylinderShape(0.5F, 1F);
	public static final CylinderShape CIRCLE_UNIT = new CylinderShape(0.5F, 0F);

	public static final MapCodec<CylinderShape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("radius").forGetter(CylinderShape::radius),
		Codec.FLOAT.optionalFieldOf("height", 0F).forGetter(CylinderShape::radius)
	).apply(instance, CylinderShape::new));

	public static final StreamCodec<ByteBuf, CylinderShape> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, CylinderShape::radius,
		KMathStreamCodecs.FLOAT_OR_ZERO, CylinderShape::height,
		CylinderShape::new
	);

	public static final ShapeType TYPE = new ShapeType("cylinder", CODEC, STREAM_CODEC);

	@Override
	public ShapeType type() {
		return TYPE;
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

			callback.line(x, y + h, z, x + cx, y + h, z + cz);
			callback.line(x + cx, y + h, z + cz, x + nx, y + h, z + nz);

			if (h > 0F) {
				callback.line(x, y - h, z, x + cx, y - h, z + cz);
				callback.line(x + cx, y - h, z + cz, x + nx, y - h, z + nz);

				callback.line(x + cx, y - h, z + cz, x + cx, y + h, z + cz, 0F, 1F, 0F);
			}
		}
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		float r = Math.max(radius, 0F);
		float h = Math.max(height, 0F) / 2F;
		double rs = Math.PI * 2D / 24D;

		for (int i = 0; i < 24; i += 2) {
			float cx = (float) (Math.cos(i * rs) * r);
			float cz = (float) (Math.sin(i * rs) * r);
			float nx = (float) (Math.cos((i + 1D) * rs) * r);
			float nz = (float) (Math.sin((i + 1D) * rs) * r);
			float nnx = (float) (Math.cos((i + 2D) * rs) * r);
			float nnz = (float) (Math.sin((i + 2D) * rs) * r);

			callback.acceptPos(x, y + h, z).acceptNormal(0F, 1F, 0F);
			callback.acceptPos(cx, y + h, cz).acceptNormal(0F, 1F, 0F);
			callback.acceptPos(nx, y + h, nz).acceptNormal(0F, 1F, 0F);
			callback.acceptPos(nnx, y + h, nnz).acceptNormal(0F, 1F, 0F);

			if (h > 0F) {
				callback.acceptPos(x, y - h, z).acceptNormal(0F, -1F, 0F);
				callback.acceptPos(nnx, y - h, nnz).acceptNormal(0F, -1F, 0F);
				callback.acceptPos(nx, y - h, nz).acceptNormal(0F, -1F, 0F);
				callback.acceptPos(cx, y - h, cz).acceptNormal(0F, -1F, 0F);
			}
		}
	}
}
