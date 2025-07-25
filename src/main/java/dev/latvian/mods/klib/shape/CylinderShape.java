package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3fc;

public record CylinderShape(float radius, float height) implements Shape {
	public static final CylinderShape UNIT = new CylinderShape(0.5F, 1F);

	public static final MapCodec<CylinderShape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("radius").forGetter(CylinderShape::radius),
		Codec.FLOAT.optionalFieldOf("height", 0F).forGetter(CylinderShape::radius)
	).apply(instance, CylinderShape::new));

	public static final StreamCodec<ByteBuf, CylinderShape> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, CylinderShape::radius,
		KLibStreamCodecs.FLOAT_OR_ZERO, CylinderShape::height,
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
		buildLines(x, y, z, callback, radius, height, 96, 8);
	}

	public static void buildLines(float x, float y, float z, VertexCallback callback, float radius, float height, int detail, int hdetail) {
		float r = Math.max(radius, 0F);
		float h = Math.max(height, 0F) / 2F;
		double rs = Math.PI * 2D / (double) detail;

		for (int i = 0; i < detail; i++) {
			float cx = (float) (Math.cos(i * rs) * r);
			float cz = (float) (Math.sin(i * rs) * r);
			float nx = (float) (Math.cos((i + 1D) * rs) * r);
			float nz = (float) (Math.sin((i + 1D) * rs) * r);

			callback.line(x + cx, y + h, z + cz, x + nx, y + h, z + nz);

			if (h > 0F) {
				callback.line(x + cx, y - h, z + cz, x + nx, y - h, z + nz);
			}
		}

		if (h > 0F) {
			double rsv = Math.PI * 2D / (double) hdetail;

			for (int i = 0; i < hdetail; i++) {
				float cx = (float) (Math.cos(i * rsv) * r);
				float cz = (float) (Math.sin(i * rsv) * r);
				callback.line(x + cx, y - h, z + cz, x + cx, y + h, z + cz, 0F, 1F, 0F);
			}
		}
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		buildQuads(x, y, z, callback, radius, height, 96);
	}

	public static void buildQuads(float x, float y, float z, VertexCallback callback, float radius, float height, int detail) {
		float r = Math.max(radius, 0F);
		float h = Math.max(height, 0F) / 2F;
		double rs = Math.PI * 2D / (double) detail;

		for (int i = 0; i < detail; i += 2) {
			float cx = (float) Math.cos(i * rs);
			float cz = (float) Math.sin(i * rs);
			float nx = (float) Math.cos((i + 1D) * rs);
			float nz = (float) Math.sin((i + 1D) * rs);
			float nnx = (float) Math.cos((i + 2D) * rs);
			float nnz = (float) Math.sin((i + 2D) * rs);

			callback.quad(x, y + h, z, 0.5F, 0.5F, 0F, 1F, 0F);
			callback.quad(x + nnx * r, y + h, z + nnz * r, 0.5F + nnx / r / 2F, 0.5F + nnz / 2F, 0F, 1F, 0F);
			callback.quad(x + nx * r, y + h, z + nz * r, 0.5F + nx / 2F, 0.5F + nz / 2F, 0F, 1F, 0F);
			callback.quad(x + cx * r, y + h, z + cz * r, 0.5F + cx / 2F, 0.5F + cz / 2F, 0F, 1F, 0F);

			if (h > 0F) {
				callback.quad(x, y - h, z, 0.5F, 0.5F, 0F, -1F, 0F);
				callback.quad(x + cx * r, y - h, z + cz * r, 0.5F + cx / 2F, 0.5F + cz / 2F, 0F, -1F, 0F);
				callback.quad(x + nx * r, y - h, z + nz * r, 0.5F + nx / 2F, 0.5F + nz / 2F, 0F, -1F, 0F);
				callback.quad(x + nnx * r, y - h, z + nnz * r, 0.5F + nnx / 2F, 0.5F + nnz / 2F, 0F, -1F, 0F);

				float nrmx = (float) Math.cos((i + 0.5D) * rs);
				float nrmz = (float) Math.sin((i + 0.5D) * rs);
				float nnrmx = (float) Math.cos((i + 1.5D) * rs);
				float nnrmz = (float) Math.sin((i + 1.5D) * rs);
				float cu = i * 3F / (float) detail;
				float nu = (i + 1F) * 3F / (float) detail;
				float nnu = (i + 2F) * 3F / (float) detail;

				callback.quad(x + nx * r, y + h, z + nz * r, nu, 0F, nrmx, 0F, nrmz);
				callback.quad(x + nx * r, y - h, z + nz * r, nu, 1F, nrmx, 0F, nrmz);
				callback.quad(x + cx * r, y - h, z + cz * r, cu, 1F, nrmx, 0F, nrmz);
				callback.quad(x + cx * r, y + h, z + cz * r, cu, 0F, nrmx, 0F, nrmz);

				callback.quad(x + nnx * r, y + h, z + nnz * r, nnu, 0F, nnrmx, 0F, nnrmz);
				callback.quad(x + nnx * r, y - h, z + nnz * r, nnu, 1F, nnrmx, 0F, nnrmz);
				callback.quad(x + nx * r, y - h, z + nz * r, nu, 1F, nnrmx, 0F, nnrmz);
				callback.quad(x + nx * r, y + h, z + nz * r, nu, 0F, nnrmx, 0F, nnrmz);
			}
		}
	}

	@Override
	public boolean contains(Vector3fc p) {
		if (height <= 0F) {
			return p.lengthSquared() <= radius * radius;
		}

		float h = height / 2F;

		if (p.y() < -h || p.y() > h) {
			return false;
		}

		float dx = p.x();
		float dz = p.z();
		return dx * dx + dz * dz <= radius * radius;
	}
}
