package dev.latvian.mods.klib.shape;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.math.Rotation;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record RotatedShape(Ref<Shape> shape, Rotation rotation) implements Shape {
	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"rotated",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Shape.CODEC.fieldOf("shape").forGetter(RotatedShape::shape),
			Rotation.CODEC.optionalFieldOf("rotation", Rotation.NONE).forGetter(RotatedShape::rotation)
		).apply(instance, RotatedShape::new)),
		CompositeStreamCodec.of(
			Shape.STREAM_CODEC, RotatedShape::shape,
			Rotation.STREAM_CODEC, RotatedShape::rotation,
			RotatedShape::new
		)
	);

	@Override
	public CustomRegistryType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public Shape optimize() {
		return rotation.isNone() ? shape.value() : this;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
	}

	@Override
	public boolean contains(Vector3fc p) {
		var mat = new Matrix4f();
		rotation.rotateYXZ(mat);
		var vec = new Vector3f(p.x(), p.y(), p.z()).mulPosition(mat);
		return shape.value().contains(vec);
	}
}
