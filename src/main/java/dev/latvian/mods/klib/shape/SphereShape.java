package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Mth;

public record SphereShape(float size, AAFBB box) implements Shape {
	public static final UnitType<ByteBuf, Shape> UNIT_SPHERE = UnitType.create("unit_sphere", new SphereShape(1F));

	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"sphere",
		"size",
		Codec.FLOAT,
		ByteBufCodecs.FLOAT,
		SphereShape::new,
		SphereShape::size
	);

	public SphereShape(float size) {
		this(size, AAFBB.centered(size));
	}

	@Override
	public DynamicType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public void buildLines(LineVertexCallback callback) {
		SpherePoints.M.buildLines(size, callback);
	}

	@Override
	public void buildOuterLines(LineVertexCallback callback) {
		callback.cuboidLines(box);
	}

	@Override
	public void buildQuads(QuadVertexCallback callback) {
		SpherePoints.M.buildQuads(size, callback);
	}

	@Override
	public boolean contains(float x, float y, float z) {
		return x * x + y * y + z * z <= Mth.square(size / 2F);
	}

	@Override
	public Ref<Shape> optimize(Ref<Shape> self) {
		return size == 1F ? UNIT_SPHERE : self;
	}

	@Override
	public float intersects(float x0, float y0, float z0, float x1, float y1, float z1) {
		float dx = x1 - x0;
		float dy = y1 - y0;
		float dz = z1 - z0;

		float a = dx * dx + dy * dy + dz * dz;
		float b = 2F * (x0 * dx + y0 * dy + z0 * dz);
		float c = (x0 * x0 + y0 * y0 + z0 * z0) - Mth.square(size / 2F);

		float discriminant = b * b - 4F * a * c;

		if (discriminant < 0D) {
			return Float.NaN;
		}

		var dsq = Math.sqrt(discriminant);
		double t1 = (-b - dsq) / (2D * a);
		double t2 = (-b + dsq) / (2D * a);

		if (t1 >= 0D && t1 <= 1D && t2 >= 0D && t2 <= 1D) {
			return (float) Math.min(t1, t2);
		} else if (t1 >= 0D && t1 <= 1D) {
			return (float) t1;
		} else if (t2 >= 0D && t2 <= 1D) {
			return (float) t2;
		} else {
			return Float.NaN;
		}
	}
}
