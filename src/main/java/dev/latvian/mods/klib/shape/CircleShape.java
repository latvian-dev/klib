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
		buildTopQuads(x, y, z, callback, radius, 96);
	}

	public static void buildTopQuads(float x, float y, float z, VertexCallback callback, float radius, int detail) {
		float r = Math.max(radius, 0F);
		double rs = Math.PI * 2D / (double) detail;

		for (int i = 0; i < detail; i += 2) {
			float cx = (float) Math.cos(i * rs);
			float cz = (float) Math.sin(i * rs);
			float nx = (float) Math.cos((i + 1D) * rs);
			float nz = (float) Math.sin((i + 1D) * rs);
			float nnx = (float) Math.cos((i + 2D) * rs);
			float nnz = (float) Math.sin((i + 2D) * rs);

			callback.quad(x, y, z, 0.5F, 0.5F, 0F, 1F, 0F);
			callback.quad(x + nnx * r, y, z + nnz * r, 0.5F + nnx / 2F, 0.5F + nnz / 2F, 0F, 1F, 0F);
			callback.quad(x + nx * r, y, z + nz * r, 0.5F + nx / 2F, 0.5F + nz / 2F, 0F, 1F, 0F);
			callback.quad(x + cx * r, y, z + cz * r, 0.5F + cx / 2F, 0.5F + cz / 2F, 0F, 1F, 0F);
		}
	}

	public static void buildBottomQuads(float x, float y, float z, VertexCallback callback, float radius, int detail) {
		float r = Math.max(radius, 0F);
		double rs = Math.PI * 2D / (double) detail;

		for (int i = 0; i < detail; i += 2) {
			float cx = (float) Math.cos(i * rs);
			float cz = (float) Math.sin(i * rs);
			float nx = (float) Math.cos((i + 1D) * rs);
			float nz = (float) Math.sin((i + 1D) * rs);
			float nnx = (float) Math.cos((i + 2D) * rs);
			float nnz = (float) Math.sin((i + 2D) * rs);

			callback.quad(x, y, z, 0.5F, 0.5F, 0F, -1F, 0F);
			callback.quad(x + cx * r, y, z + cz * r, 0.5F + cx / 2F, 0.5F + cz / 2F, 0F, -1F, 0F);
			callback.quad(x + nx * r, y, z + nz * r, 0.5F + nx / 2F, 0.5F + nz / 2F, 0F, -1F, 0F);
			callback.quad(x + nnx * r, y, z + nnz * r, 0.5F + nnx / 2F, 0.5F + nnz / 2F, 0F, -1F, 0F);
		}
	}

	@Override
	public boolean contains(Vector3fc p) {
		return p.y() == 0F && p.lengthSquared() <= radius * radius;
	}
}
