package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record InverseInterpolation(Interpolation interpolation) implements Interpolation {
	public static final MapCodec<InverseInterpolation> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Interpolation.CODEC.fieldOf("interpolation").forGetter(InverseInterpolation::interpolation)
	).apply(instance, InverseInterpolation::new));

	public static final StreamCodec<ByteBuf, InverseInterpolation> STREAM_CODEC = CompositeStreamCodec.of(
		Interpolation.STREAM_CODEC, InverseInterpolation::interpolation,
		InverseInterpolation::new
	);

	public static final InterpolationType<InverseInterpolation> TYPE = InterpolationType.of("inverse", MAP_CODEC, STREAM_CODEC);

	@Override
	public InterpolationType<?> type() {
		return TYPE;
	}

	@Override
	public double interpolate(double t) {
		return 1D - interpolation.interpolate(1D - t);
	}

	@Override
	public float interpolate(float t) {
		return 1F - interpolation.interpolate(1F - t);
	}

	@Override
	public @NotNull String toString() {
		return "Inverse[" + interpolation + "]";
	}
}
