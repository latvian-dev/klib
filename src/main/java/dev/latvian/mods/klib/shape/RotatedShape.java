package dev.latvian.mods.klib.shape;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.math.Rotation;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record RotatedShape(Ref<Shape> shape, Rotation rotation) implements Shape {
	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"rotated",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Shape.CODEC.fieldOf("shape").forGetter(RotatedShape::shape),
			Rotation.CODEC_WITH_ROLL.optionalFieldOf("rotation", Rotation.NONE).forGetter(RotatedShape::rotation)
		).apply(instance, RotatedShape::new)),
		CompositeStreamCodec.of(
			Shape.STREAM_CODEC, RotatedShape::shape,
			Rotation.STREAM_CODEC_WITH_ROLL, RotatedShape::rotation,
			RotatedShape::new
		)
	);

	@Override
	public DynamicType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public Ref<Shape> optimize(Ref<Shape> self) {
		return rotation.isNone() ? shape.optimizedRef() : self;
	}

	@Override
	public void buildLines(LineVertexCallback callback) {
		// FIXME
		shape.value().buildLines(callback);
	}

	@Override
	public void buildQuads(QuadVertexCallback callback) {
		// FIXME
		shape.value().buildQuads(callback);
	}

	@Override
	public boolean contains(Vector3fc p) {
		var mat = new Matrix4f();
		rotation.rotateYXZ(mat);
		var vec = new Vector3f(p.x(), p.y(), p.z()).mulPosition(mat);
		return shape.value().contains(vec);
	}
}
