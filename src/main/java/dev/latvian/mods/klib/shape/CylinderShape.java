package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Mth;

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
	public void buildLines(LineVertexCallback callback) {
		callback.cylinderLines(width, height, 96, 8);
	}

	@Override
	public void buildOuterLines(LineVertexCallback callback) {
		callback.cuboidLines(box);
	}

	@Override
	public void buildQuads(QuadVertexCallback callback) {
		callback.cylinderQuads(width, height, 96);
	}

	@Override
	public boolean contains(float x, float y, float z) {
		return Math.abs(y) <= height / 2F && x * x + z * z <= Mth.square(width / 2F);
	}

	@Override
	public float intersects(float x0, float y0, float z0, float x1, float y1, float z1) {
		return Float.NaN;
	}
}
