package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.JOMLCodecs;
import dev.latvian.mods.klib.codec.JOMLStreamCodecs;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.registry.DynamicType;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2fc;

public record BezierInterpolation(Vector2fc a, Vector2fc b) implements Interpolation {
	public static final DynamicType<ByteBuf, Interpolation> TYPE = DynamicType.create(
		"bezier",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			JOMLCodecs.VEC2C.fieldOf("a").forGetter(BezierInterpolation::a),
			JOMLCodecs.VEC2C.fieldOf("b").forGetter(BezierInterpolation::b)
		).apply(instance, BezierInterpolation::new)),
		CompositeStreamCodec.of(
			JOMLStreamCodecs.VEC2C, BezierInterpolation::a,
			JOMLStreamCodecs.VEC2C, BezierInterpolation::b,
			BezierInterpolation::new
		)
	);

	@Override
	public DynamicType<ByteBuf, Interpolation> type() {
		return TYPE;
	}

	@Override
	public double interpolate(double t) {
		return KMath.linearizedBezierY((float) t, a.x(), a.y(), b.x(), b.y());
	}

	@Override
	public float interpolate(float t) {
		return KMath.linearizedBezierY(t, a.x(), a.y(), b.x(), b.y());
	}

	@Override
	public @NotNull String toString() {
		return "Bezier[(%.03f, %.03f) & (%.03f, %.03f)]".formatted(a.x(), a.y(), b.x(), b.y());
	}

	@Override
	public boolean isLinear() {
		return a.x() == a.y() && b.x() == b.y();
	}
}
