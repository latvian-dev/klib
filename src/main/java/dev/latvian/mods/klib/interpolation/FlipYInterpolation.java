package dev.latvian.mods.klib.interpolation;

import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public record FlipYInterpolation(Ref<Interpolation> interpolation) implements Interpolation {
	public static final DynamicType<ByteBuf, Interpolation> TYPE = DynamicType.create(
		"flip_y",
		"interpolation",
		Interpolation.CODEC,
		Interpolation.STREAM_CODEC,
		FlipYInterpolation::new,
		FlipYInterpolation::interpolation
	);

	@Override
	public DynamicType<ByteBuf, Interpolation> type() {
		return TYPE;
	}

	@Override
	public float interpolate(float x) {
		return 1F - interpolation.value().interpolate(x);
	}

	@Override
	public @NotNull String toString() {
		return "FlipY[" + interpolation + "]";
	}
}
