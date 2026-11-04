package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.math.HitBuilder;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;

public record SphereShape(float size, float radius, AAFBB box) implements Shape {
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
		this(size, size / 2F, AAFBB.centered(size));
	}

	@Override
	public DynamicType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public Ref<Shape> optimize(Ref<Shape> self) {
		return size == 1F ? UNIT_SPHERE : self;
	}

	@Override
	public void buildLines(Vector3fc offset, LineVertexCallback callback) {
		SpherePoints.M.buildLines(size, offset, callback);
	}

	@Override
	public void buildOuterLines(Vector3fc offset, LineVertexCallback callback) {
		callback.cuboidLines(box.move(offset));
	}

	@Override
	public void buildQuads(Vector3fc offset, QuadVertexCallback callback) {
		SpherePoints.M.buildQuads(size, offset, callback);
	}

	@Override
	public boolean contains(float x, float y, float z) {
		return x * x + y * y + z * z <= radius * radius;
	}

	@Override
	public boolean hit(Vec3 pos, HitBuilder hit) {
		double Lx = pos.x - hit.from.x;
		double Ly = pos.y - hit.from.y;
		double Lz = pos.z - hit.from.z;
		double tca = Lx * (hit.to.x - hit.from.x) + Ly * (hit.to.y - hit.from.y) + Lz * (hit.to.z - hit.from.z);
		double d2 = Lx * Lx + Ly * Ly + Lz * Lz - tca * tca;

		if (d2 > radius * radius) {
			return false;
		}

		double thc = Math.sqrt(radius * radius - d2);
		double t0 = tca - thc;
		double t1 = tca + thc;

		if (t1 >= 0D && t1 >= t0) {
			hit.offer(t0, null);
			return true;
		}

		return false;
	}

	@Override
	public boolean intersects(AAFBB box) {
		double dx = Math.clamp(0F, box.minX(), box.maxX());
		double dy = Math.clamp(0F, box.minY(), box.maxY());
		double dz = Math.clamp(0F, box.minZ(), box.maxZ());
		return dx * dx + dy * dy + dz * dz <= radius * radius;
	}

	@Override
	public double closestDistanceTo(float x, float y, float z) {
		return Math.max(0D, Math.sqrt(x * x + y * y + z * z) - radius);
	}
}
