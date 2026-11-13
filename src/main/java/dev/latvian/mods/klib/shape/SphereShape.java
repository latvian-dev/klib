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
import org.joml.Intersectiond;
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
		SpherePoints.get(radius).buildLines(size, offset, callback);
	}

	@Override
	public void buildOuterLines(Vector3fc offset, LineVertexCallback callback) {
		callback.cuboidLines(box.move(offset));
	}

	@Override
	public void buildQuads(Vector3fc offset, QuadVertexCallback callback) {
		SpherePoints.get(radius).buildQuads(size, offset, callback);
	}

	@Override
	public boolean contains(float x, float y, float z) {
		return x * x + y * y + z * z <= radius * radius;
	}

	@Override
	public boolean hit(Vec3 pos, HitBuilder hit) {
		if (pos.distanceToSqr(hit.from) <= radius * radius) {
			return false;
		} else if (Intersectiond.intersectRaySphere(
			hit.from.x,
			hit.from.y,
			hit.from.z,
			hit.normalizedDelta.x,
			hit.normalizedDelta.y,
			hit.normalizedDelta.z,
			pos.x,
			pos.y,
			pos.z,
			radius * radius,
			hit.temp2d
		)) {
			if (hit.temp2d.x <= hit.totalDistance) {
				hit.offer(hit.from.add(hit.normalizedDelta.scale(hit.temp2d.x)));
				return true;
			}
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
