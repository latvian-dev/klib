package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record FlipXInterpolation(Interpolation interpolation) implements Interpolation {
	public static final MapCodec<FlipXInterpolation> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Interpolation.CODEC.fieldOf("interpolation").forGetter(FlipXInterpolation::interpolation)
	).apply(instance, FlipXInterpolation::new));

	public static final StreamCodec<ByteBuf, FlipXInterpolation> STREAM_CODEC = CompositeStreamCodec.of(
		Interpolation.STREAM_CODEC, FlipXInterpolation::interpolation,
		FlipXInterpolation::new
	);

	public static final InterpolationType<FlipXInterpolation> TYPE = InterpolationType.of("flip_x", MAP_CODEC, STREAM_CODEC);

	@Override
	public InterpolationType<?> type() {
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
