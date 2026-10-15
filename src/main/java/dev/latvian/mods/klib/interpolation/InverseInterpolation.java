package dev.latvian.mods.klib.interpolation;

import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public record InverseInterpolation(Ref<Interpolation> interpolation) implements Interpolation {
	public static final DynamicType<ByteBuf, Interpolation> TYPE = DynamicType.create(
		"inverse",
		"interpolation",
		Interpolation.CODEC,
		Interpolation.STREAM_CODEC,
		InverseInterpolation::new,
		InverseInterpolation::interpolation
	);

	@Override
	public DynamicType<ByteBuf, Interpolation> type() {
		return TYPE;
	}

	@Override
	public float interpolate(float x) {
		return 1F - interpolation.value().interpolate(1F - x);
	}

	@Override
	public @NotNull String toString() {
		return "Inverse[" + interpolation + "]";
	}
}
