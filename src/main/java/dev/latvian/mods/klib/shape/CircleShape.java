package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3fc;

public record CircleShape(float radius) implements Shape {
	public static final CircleShape UNIT = new CircleShape(0.5F);

	public static final MapCodec<CircleShape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("radius").forGetter(CircleShape::radius)
	).apply(instance, CircleShape::new));

	public static final StreamCodec<ByteBuf, CircleShape> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, CircleShape::radius,
		CircleShape::new
	);

	public static final ShapeType TYPE = new ShapeType("circle", CODEC, STREAM_CODEC);

	@Override
	public ShapeType type() {
		return TYPE;
	}

	@Override
	public Shape optimize() {
		if (radius <= 0F) {
			return EmptyShape.INSTANCE;
		}

		return this;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
		buildLines(x, y, z, callback, radius, 96);
	}

	public static void buildLines(float x, float y, float z, VertexCallback callback, float radius, int detail) {
		float r = Math.max(radius, 0F);
		double rs = Math.PI * 2D / (double) detail;

		for (int i = 0; i < detail; i++) {
			float cx = (float) (Math.cos(i * rs) * r);
			float cz = (float) (Math.sin(i * rs) * r);
			float nx = (float) (Math.cos((i + 1D) * rs) * r);
			float nz = (float) (Math.sin((i + 1D) * rs) * r);

			callback.line(x + cx, y, z + cz, x + nx, y, z + nz);
		}
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		buildQuads(x, y, z, callback, radius, 96);
	}

	public static void buildQuads(float x, float y, float z, VertexCallback callback, float radius, int detail) {
		float r = Math.max(radius, 0F);
		double rs = Math.PI * 2D / (double) detail;

		for (int i = 0; i < detail; i += 2) {
			float cx = (float) (Math.cos(i * rs) * r);
			float cz = (float) (Math.sin(i * rs) * r);
			float nx = (float) (Math.cos((i + 1D) * rs) * r);
			float nz = (float) (Math.sin((i + 1D) * rs) * r);
			float nnx = (float) (Math.cos((i + 2D) * rs) * r);
			float nnz = (float) (Math.sin((i + 2D) * rs) * r);

			callback.acceptPos(x, y, z).acceptNormal(0F, 1F, 0F).acceptTex(0.5F, 0.5F);
			callback.acceptPos(x + nnx * r, y, z + nnz * r).acceptNormal(0F, 1F, 0F).acceptTex(0.5F + nnx / 2F, 0.5F + nnz / 2F);
			callback.acceptPos(x + nx * r, y, z + nz * r).acceptNormal(0F, 1F, 0F).acceptTex(0.5F + nx / 2F, 0.5F + nz / 2F);
			callback.acceptPos(x + cx * r, y, z + cz * r).acceptNormal(0F, 1F, 0F).acceptTex(0.5F + cx / 2F, 0.5F + cz / 2F);
		}
	}

	@Override
	public boolean contains(Vector3fc p) {
		return p.y() == 0F && p.lengthSquared() <= radius * radius;
	}
}
