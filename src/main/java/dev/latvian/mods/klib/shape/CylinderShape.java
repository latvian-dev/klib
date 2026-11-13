package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.math.HitBuilder;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;

public record CylinderShape(float width, float height, AAFBB box) implements Shape {
	public static final UnitType<ByteBuf, Shape> UNIT_CYLINDER = UnitType.create("unit_cylinder", new CylinderShape(1F, 1F));
	public static final UnitType<ByteBuf, Shape> UNIT_CIRCLE = UnitType.create("unit_circle", new CylinderShape(1F, 0F));

	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"cylinder",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.FLOAT.optionalFieldOf("width", 1F).forGetter(CylinderShape::width),
			Codec.FLOAT.optionalFieldOf("height", 1F).forGetter(CylinderShape::height)
		).apply(instance, CylinderShape::new)),
		CompositeStreamCodec.of(
			ByteBufCodecs.FLOAT, CylinderShape::width,
			ByteBufCodecs.FLOAT, CylinderShape::height,
			CylinderShape::new
		)
	);

	public CylinderShape(float width, float height) {
		this(width, height, AAFBB.centered(width, height, width));
	}

	@Override
	public DynamicType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public Ref<Shape> optimize(Ref<Shape> self) {
		if (width <= 0F && height <= 0F) {
			return EMPTY;
		} else if (width == 1F && height == 1F) {
			return UNIT_CYLINDER;
		} else if (width == 1F && height == 0F) {
			return UNIT_CIRCLE;
		} else {
			return self;
		}
	}

	@Override
	public void buildLines(Vector3fc offset, LineVertexCallback callback) {
		callback.cylinderLines(offset, width, height, 96, 8);
	}

	@Override
	public void buildOuterLines(Vector3fc offset, LineVertexCallback callback) {
		callback.cuboidLines(box.move(offset));
	}

	@Override
	public void buildQuads(Vector3fc offset, QuadVertexCallback callback) {
		callback.cylinderQuads(offset, width, height, 96);
	}

	@Override
	public boolean contains(float x, float y, float z) {
		return Math.abs(y) <= height / 2F && x * x + z * z <= Mth.square(width / 2F);
	}

	@Override
	public boolean hit(Vec3 pos, HitBuilder hit) {
		if (width <= 0F || height <= 0F) {
			return false;
		}

		var radius = width / 2D;
		var radiusSq = radius * radius;

		var minY = pos.y - height / 2D;
		var maxY = pos.y + height / 2D;

		var ox = hit.from.x - pos.x;
		var oz = hit.from.z - pos.z;

		var dx = hit.delta.x;
		var dy = hit.delta.y;
		var dz = hit.delta.z;

		var distSq = ox * ox + oz * oz;

		if (distSq <= radiusSq && hit.from.y >= minY && hit.from.y <= maxY) {
			return false;
		}

		var nearest = Double.POSITIVE_INFINITY;

		var a = dx * dx + dz * dz;
		var b = 2D * (ox * dx + oz * dz);
		var c = distSq - radiusSq;

		if (a > 0D) {
			var discriminant = b * b - 4D * a * c;

			if (discriminant >= 0D) {
				var sqrt = Math.sqrt(discriminant);

				var t1 = (-b - sqrt) / (2D * a);
				var t2 = (-b + sqrt) / (2D * a);

				if (t1 >= 0D && t1 <= 1D) {
					var y = hit.from.y + dy * t1;

					if (y >= minY && y <= maxY) {
						nearest = t1;
					}
				}

				if (t2 >= 0D && t2 <= 1D && t2 < nearest) {
					var y = hit.from.y + dy * t2;

					if (y >= minY && y <= maxY) {
						nearest = t2;
					}
				}
			}
		}

		if (dy != 0D) {
			var tBottom = (minY - hit.from.y) / dy;

			if (tBottom >= 0D && tBottom <= 1D && tBottom < nearest) {
				var x = ox + dx * tBottom;
				var z = oz + dz * tBottom;

				if (x * x + z * z <= radiusSq) {
					nearest = tBottom;
				}
			}

			var tTop = (maxY - hit.from.y) / dy;

			if (tTop >= 0D && tTop <= 1D && tTop < nearest) {
				var x = ox + dx * tTop;
				var z = oz + dz * tTop;

				if (x * x + z * z <= radiusSq) {
					nearest = tTop;
				}
			}
		}

		if (nearest > 1D) {
			return false;
		}

		hit.offer(hit.from.lerp(hit.to, nearest));
		return true;
	}
}
