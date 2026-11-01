package dev.latvian.mods.klib.shape;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.math.ComputedTransformation;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3f;

public record TransformedShape(Ref<Shape> shape, ComputedTransformation transformation, AAFBB box) implements Shape {
	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"transformed",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Shape.CODEC.fieldOf("shape").forGetter(TransformedShape::shape),
			ComputedTransformation.CODEC.fieldOf("transformation").forGetter(TransformedShape::transformation)
		).apply(instance, TransformedShape::new)),
		CompositeStreamCodec.of(
			Shape.STREAM_CODEC, TransformedShape::shape,
			ComputedTransformation.STREAM_CODEC, TransformedShape::transformation,
			TransformedShape::new
		)
	);

	public TransformedShape(Ref<Shape> shape, ComputedTransformation transformation) {
		this(shape, transformation, AAFBB.FULL); // FIXME
	}

	@Override
	public DynamicType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public Ref<Shape> optimize(Ref<Shape> self) {
		if (transformation.identity()) {
			return shape.optimizedRef();
		} else {
			return self;
		}
	}

	@Override
	public void buildLines(LineVertexCallback callback) {
		shape.value().buildLines(transformation.transformLines(callback));
	}

	@Override
	public void buildOuterLines(LineVertexCallback callback) {
		callback.cuboidLines(box);
	}

	@Override
	public void buildQuads(QuadVertexCallback callback) {
		shape.value().buildQuads(transformation.transformQuads(callback));
	}

	@Override
	public boolean contains(float x, float y, float z) {
		var vec = new Vector3f(x, y, z);
		vec.mulPosition(transformation.matrix());
		return shape.value().contains(vec.x, vec.y, vec.z);
	}
}
