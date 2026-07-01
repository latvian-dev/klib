package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.util.ID;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public record FlipXInterpolation(Interpolation interpolation) implements Interpolation {
	public static final CustomRegistryType<ByteBuf, Interpolation> TYPE = Interpolation.REGISTRY.dynamic(ID.klib("flip_x"),
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
		return interpolation.interpolate(1D - t);
	}

	@Override
	public float interpolate(float t) {
		return interpolation.interpolate(1F - t);
	}

	@Override
	public @NotNull String toString() {
		return "FlipX[" + interpolation + "]";
	}
}
