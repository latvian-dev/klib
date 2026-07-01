package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.util.ID;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public record FlipYInterpolation(Interpolation interpolation) implements Interpolation {
	public static final CustomRegistryType<ByteBuf, Interpolation> TYPE = Interpolation.REGISTRY.dynamic(ID.klib("flip_y"),
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Interpolation.CODEC.fieldOf("interpolation").forGetter(FlipYInterpolation::interpolation)
		).apply(instance, FlipYInterpolation::new)),
		CompositeStreamCodec.of(
			Interpolation.STREAM_CODEC, FlipYInterpolation::interpolation,
			FlipYInterpolation::new
		)
	);

	@Override
	public CustomRegistryType<ByteBuf, Interpolation> type() {
		return TYPE;
	}

	@Override
	public double interpolate(double t) {
		return 1D - interpolation.interpolate(t);
	}

	@Override
	public float interpolate(float t) {
		return 1F - interpolation.interpolate(t);
	}

	@Override
	public @NotNull String toString() {
		return "FlipY[" + interpolation + "]";
	}
}
