package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record CompositeInterpolation(Interpolation in, Interpolation out) implements Interpolation {
	public static final CompositeInterpolation SINE = new CompositeInterpolation(EaseIn.SINE, EaseOut.SINE);
	public static final CompositeInterpolation QUAD = new CompositeInterpolation(EaseIn.QUAD, EaseOut.QUAD);
	public static final CompositeInterpolation CUBIC = new CompositeInterpolation(EaseIn.CUBIC, EaseOut.CUBIC);
	public static final CompositeInterpolation QUART = new CompositeInterpolation(EaseIn.QUART, EaseOut.QUART);
	public static final CompositeInterpolation QUINT = new CompositeInterpolation(EaseIn.QUINT, EaseOut.QUINT);
	public static final CompositeInterpolation EXPO = new CompositeInterpolation(EaseIn.EXPO, EaseOut.EXPO);
	public static final CompositeInterpolation CIRC = new CompositeInterpolation(EaseIn.CIRC, EaseOut.CIRC);
	public static final CompositeInterpolation BACK = new CompositeInterpolation(EaseIn.BACK, EaseOut.BACK);
	public static final CompositeInterpolation ELASTIC = new CompositeInterpolation(EaseIn.ELASTIC, EaseOut.ELASTIC);
	public static final CompositeInterpolation BOUNCE = new CompositeInterpolation(EaseIn.BOUNCE, EaseOut.BOUNCE);

	public static CompositeInterpolation of(Interpolation in, Interpolation out) {
		if (in == EaseIn.SINE && out == EaseOut.SINE) {
			return SINE;
		} else if (in == EaseIn.QUAD && out == EaseOut.QUAD) {
			return QUAD;
		} else if (in == EaseIn.CUBIC && out == EaseOut.CUBIC) {
			return CUBIC;
		} else if (in == EaseIn.QUART && out == EaseOut.QUART) {
			return QUART;
		} else if (in == EaseIn.QUINT && out == EaseOut.QUINT) {
			return QUINT;
		} else if (in == EaseIn.EXPO && out == EaseOut.EXPO) {
			return EXPO;
		} else if (in == EaseIn.CIRC && out == EaseOut.CIRC) {
			return CIRC;
		} else if (in == EaseIn.BACK && out == EaseOut.BACK) {
			return BACK;
		} else if (in == EaseIn.ELASTIC && out == EaseOut.ELASTIC) {
			return ELASTIC;
		} else if (in == EaseIn.BOUNCE && out == EaseOut.BOUNCE) {
			return BOUNCE;
		}

		return new CompositeInterpolation(in, out);
	}

	public static final MapCodec<CompositeInterpolation> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Interpolation.CODEC.fieldOf("in").forGetter(CompositeInterpolation::in),
		Interpolation.CODEC.fieldOf("out").forGetter(CompositeInterpolation::out)
	).apply(instance, CompositeInterpolation::of));

	public static final StreamCodec<ByteBuf, CompositeInterpolation> STREAM_CODEC = CompositeStreamCodec.of(
		Interpolation.STREAM_CODEC, CompositeInterpolation::in,
		Interpolation.STREAM_CODEC, CompositeInterpolation::out,
		CompositeInterpolation::of
	);

	public static final InterpolationType<?>[] EASING = {
		InterpolationType.unit("sine_in_out", SINE),
		InterpolationType.unit("quad_in_out", QUAD),
		InterpolationType.unit("cubic_in_out", CUBIC),
		InterpolationType.unit("quart_in_out", QUART),
		InterpolationType.unit("quint_in_out", QUINT),
		InterpolationType.unit("expo_in_out", EXPO),
		InterpolationType.unit("circ_in_out", CIRC),
		InterpolationType.unit("back_in_out", BACK),
		InterpolationType.unit("elastic_in_out", ELASTIC),
		InterpolationType.unit("bounce_in_out", BOUNCE)
	};

	public static final InterpolationType<CompositeInterpolation> TYPE = InterpolationType.of("composite", MAP_CODEC, STREAM_CODEC);

	@Override
	public InterpolationType<?> type() {
		for (var type : EASING) {
			if (type.unit() == this) {
				return type;
			}
		}

		return TYPE;
	}

	@Override
	public double interpolate(double t) {
		return (t < 0.5D ? in.interpolate(t * 2D) : (1D + out.interpolate(t * 2D - 1D))) / 2D;
	}

	@Override
	public float interpolate(float t) {
		return (t < 0.5F ? in.interpolate(t * 2F) : (1F + out.interpolate(t * 2F - 1F))) / 2F;
	}

	@Override
	public @NotNull String toString() {
		return "Composite[" + in + " -> " + out + "]";
	}
}
