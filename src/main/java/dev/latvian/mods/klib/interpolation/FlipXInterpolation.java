package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public record FlipXInterpolation(Ref<Interpolation> interpolation) implements Interpolation {
	public static final DynamicType<ByteBuf, Interpolation> TYPE = DynamicType.create(
		"flip_x",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Interpolation.CODEC.fieldOf("interpolation").forGetter(FlipXInterpolation::interpolation)
		).apply(instance, FlipXInterpolation::new)),
		CompositeStreamCodec.of(
			Interpolation.STREAM_CODEC, FlipXInterpolation::interpolation,
			FlipXInterpolation::new
		)
	);

	@Override
	public CustomRegistryType<ByteBuf, Interpolation> type() {
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
