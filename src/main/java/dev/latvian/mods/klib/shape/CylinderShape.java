package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Mth;
import org.joml.Vector3fc;

public record CylinderShape(float width, float height) implements Shape {
	public static final UnitType<ByteBuf, Shape> UNIT_CYLINDER = UnitType.create("unit_cylinder", new CylinderShape(1F, 1F));

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

	@Override
	public DynamicType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public Shape optimize() {
		if (width <= 0F && height <= 0F) {
			return EmptyShape.INSTANCE;
		} else if (width == 1F && height == 1F) {
			return UNIT_CYLINDER.value();
		} else {
			return this;
		}
	}

	@Override
	public void buildLines(LineVertexCallback callback) {
		callback.cylinderLines(width, height, 96, 8);
	}

	@Override
	public void buildQuads(QuadVertexCallback callback) {
		callback.cylinderQuads(width, height, 96);
	}

	@Override
	public boolean contains(Vector3fc p) {
		if (height <= 0F) {
			return p.lengthSquared() <= Mth.square(width / 2F);
		}

		float h = height / 2F;

		if (p.y() < -h || p.y() > h) {
			return false;
		}

		float dx = p.x();
		float dz = p.z();
		return dx * dx + dz * dz <= Mth.square(width / 2F);
	}
}
