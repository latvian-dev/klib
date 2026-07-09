package dev.latvian.mods.klib.interpolation;

import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public record FlipXInterpolation(Ref<Interpolation> interpolation) implements Interpolation {
	public static final DynamicType<ByteBuf, Interpolation> TYPE = DynamicType.create(
		"flip_x",
		"interpolation",
		Interpolation.CODEC,
		Interpolation.STREAM_CODEC,
		FlipXInterpolation::new,
		FlipXInterpolation::interpolation
	);

	@Override
	public DynamicType<ByteBuf, Interpolation> type() {
		return TYPE;
	}

	@Override
	public double interpolate(double t) {
		return interpolation.value().interpolate(1D - t);
	}

	@Override
	public float interpolate(float t) {
		return interpolation.value().interpolate(1F - t);
	}

	@Override
	public @NotNull String toString() {
		return "FlipX[" + interpolation + "]";
	}
}
