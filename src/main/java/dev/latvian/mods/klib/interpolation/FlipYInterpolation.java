package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record FlipYInterpolation(Interpolation interpolation) implements Interpolation {
	public static final MapCodec<FlipYInterpolation> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Interpolation.CODEC.fieldOf("interpolation").forGetter(FlipYInterpolation::interpolation)
	).apply(instance, FlipYInterpolation::new));

	public static final StreamCodec<ByteBuf, FlipYInterpolation> STREAM_CODEC = CompositeStreamCodec.of(
		Interpolation.STREAM_CODEC, FlipYInterpolation::interpolation,
		FlipYInterpolation::new
	);

	public static final InterpolationType<FlipYInterpolation> TYPE = InterpolationType.of("flip_y", MAP_CODEC, STREAM_CODEC);

	@Override
	public InterpolationType<?> type() {
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
