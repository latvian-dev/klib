package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record JoinedInterpolation(Interpolation in, Interpolation out) implements Interpolation {
	public static final JoinedInterpolation SINE = new JoinedInterpolation(EaseIn.SINE, EaseOut.SINE);
	public static final JoinedInterpolation QUAD = new JoinedInterpolation(EaseIn.QUAD, EaseOut.QUAD);
	public static final JoinedInterpolation CUBIC = new JoinedInterpolation(EaseIn.CUBIC, EaseOut.CUBIC);
	public static final JoinedInterpolation QUART = new JoinedInterpolation(EaseIn.QUART, EaseOut.QUART);
	public static final JoinedInterpolation QUINT = new JoinedInterpolation(EaseIn.QUINT, EaseOut.QUINT);
	public static final JoinedInterpolation EXPO = new JoinedInterpolation(EaseIn.EXPO, EaseOut.EXPO);
	public static final JoinedInterpolation CIRC = new JoinedInterpolation(EaseIn.CIRC, EaseOut.CIRC);
	public static final JoinedInterpolation BACK = new JoinedInterpolation(EaseIn.BACK, EaseOut.BACK);
	public static final JoinedInterpolation ELASTIC = new JoinedInterpolation(EaseIn.ELASTIC, EaseOut.ELASTIC);
	public static final JoinedInterpolation BOUNCE = new JoinedInterpolation(EaseIn.BOUNCE, EaseOut.BOUNCE);

	public static JoinedInterpolation of(Interpolation in, Interpolation out) {
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

		return new JoinedInterpolation(in, out);
	}

	public static final MapCodec<JoinedInterpolation> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Interpolation.CODEC.fieldOf("in").forGetter(JoinedInterpolation::in),
		Interpolation.CODEC.fieldOf("out").forGetter(JoinedInterpolation::out)
	).apply(instance, JoinedInterpolation::of));

	public static final StreamCodec<ByteBuf, JoinedInterpolation> STREAM_CODEC = CompositeStreamCodec.of(
		Interpolation.STREAM_CODEC, JoinedInterpolation::in,
		Interpolation.STREAM_CODEC, JoinedInterpolation::out,
		JoinedInterpolation::of
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

	public static final InterpolationType<JoinedInterpolation> TYPE = InterpolationType.of("joined", MAP_CODEC, STREAM_CODEC);

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
		return t < 0.5D ? in.interpolate(t * 2D) : 1D - out.interpolate((t - 0.5D) * 2D);
	}

	@Override
	public float interpolate(float t) {
		return t < 0.5F ? in.interpolate(t * 2F) : 1F - out.interpolate((t - 0.5F) * 2F);
	}

	@Override
	public @NotNull String toString() {
		return "Joined[" + in + " -> " + out + "]";
	}
}
