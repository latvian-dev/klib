package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Mth;
import org.joml.Vector3fc;

public record CircleShape(float size) implements Shape {
	public static final UnitType<ByteBuf, Shape> UNIT_CIRCLE = UnitType.create("unit_circle", new CircleShape(1F));

	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"circle",
		"size",
		Codec.FLOAT,
		ByteBufCodecs.FLOAT,
		CircleShape::new,
		CircleShape::size
	);

	@Override
	public DynamicType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public Shape optimize() {
		if (size <= 0F) {
			return EmptyShape.INSTANCE;
		} else if (size == 1F) {
			return UNIT_CIRCLE.value();
		} else {
			return this;
		}
	}

	@Override
	public void buildLines(LineVertexCallback callback) {
		callback.circleLines(size, 96);
	}

	@Override
	public void buildQuads(QuadVertexCallback callback) {
		callback.topCircleQuads(size, 96);
		callback.bottomCircleQuads(size, 96);
	}

	@Override
	public boolean contains(Vector3fc p) {
		return Math.abs(p.y()) <= 0.0001F && p.lengthSquared() <= Mth.square(size / 2F);
	}
}
