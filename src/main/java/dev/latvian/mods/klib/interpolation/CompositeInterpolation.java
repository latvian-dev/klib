package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.util.ID;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record CompositeInterpolation(Interpolation in, Interpolation out, @Nullable CustomRegistryType<ByteBuf, Interpolation> type) implements Interpolation {
	public static final CustomRegistryType<ByteBuf, Interpolation> SINE = Interpolation.REGISTRY.unitWithType(ID.klib("sine_in_out"), type -> new CompositeInterpolation(EaseIn.SINE, EaseOut.SINE, type));
	public static final CustomRegistryType<ByteBuf, Interpolation> QUAD = Interpolation.REGISTRY.unitWithType(ID.klib("quad_in_out"), type -> new CompositeInterpolation(EaseIn.QUAD, EaseOut.QUAD, type));
	public static final CustomRegistryType<ByteBuf, Interpolation> CUBIC = Interpolation.REGISTRY.unitWithType(ID.klib("cubic_in_out"), type -> new CompositeInterpolation(EaseIn.CUBIC, EaseOut.CUBIC, type));
	public static final CustomRegistryType<ByteBuf, Interpolation> QUART = Interpolation.REGISTRY.unitWithType(ID.klib("quart_in_out"), type -> new CompositeInterpolation(EaseIn.QUART, EaseOut.QUART, type));
	public static final CustomRegistryType<ByteBuf, Interpolation> QUINT = Interpolation.REGISTRY.unitWithType(ID.klib("quint_in_out"), type -> new CompositeInterpolation(EaseIn.QUINT, EaseOut.QUINT, type));
	public static final CustomRegistryType<ByteBuf, Interpolation> EXPO = Interpolation.REGISTRY.unitWithType(ID.klib("expo_in_out"), type -> new CompositeInterpolation(EaseIn.EXPO, EaseOut.EXPO, type));
	public static final CustomRegistryType<ByteBuf, Interpolation> CIRC = Interpolation.REGISTRY.unitWithType(ID.klib("circ_in_out"), type -> new CompositeInterpolation(EaseIn.CIRC, EaseOut.CIRC, type));
	public static final CustomRegistryType<ByteBuf, Interpolation> BACK = Interpolation.REGISTRY.unitWithType(ID.klib("back_in_out"), type -> new CompositeInterpolation(EaseIn.BACK, EaseOut.BACK, type));
	public static final CustomRegistryType<ByteBuf, Interpolation> ELASTIC = Interpolation.REGISTRY.unitWithType(ID.klib("elastic_in_out"), type -> new CompositeInterpolation(EaseIn.ELASTIC, EaseOut.ELASTIC, type));
	public static final CustomRegistryType<ByteBuf, Interpolation> BOUNCE = Interpolation.REGISTRY.unitWithType(ID.klib("bounce_in_out"), type -> new CompositeInterpolation(EaseIn.BOUNCE, EaseOut.BOUNCE, type));

	public static CompositeInterpolation of(Interpolation in, Interpolation out) {
		if (in == EaseIn.SINE && out == EaseOut.SINE) {
			return (CompositeInterpolation) SINE.instance();
		} else if (in == EaseIn.QUAD && out == EaseOut.QUAD) {
			return (CompositeInterpolation) QUAD.instance();
		} else if (in == EaseIn.CUBIC && out == EaseOut.CUBIC) {
			return (CompositeInterpolation) CUBIC.instance();
		} else if (in == EaseIn.QUART && out == EaseOut.QUART) {
			return (CompositeInterpolation) QUART.instance();
		} else if (in == EaseIn.QUINT && out == EaseOut.QUINT) {
			return (CompositeInterpolation) QUINT.instance();
		} else if (in == EaseIn.EXPO && out == EaseOut.EXPO) {
			return (CompositeInterpolation) EXPO.instance();
		} else if (in == EaseIn.CIRC && out == EaseOut.CIRC) {
			return (CompositeInterpolation) CIRC.instance();
		} else if (in == EaseIn.BACK && out == EaseOut.BACK) {
			return (CompositeInterpolation) BACK.instance();
		} else if (in == EaseIn.ELASTIC && out == EaseOut.ELASTIC) {
			return (CompositeInterpolation) ELASTIC.instance();
		} else if (in == EaseIn.BOUNCE && out == EaseOut.BOUNCE) {
			return (CompositeInterpolation) BOUNCE.instance();
		}

		return new CompositeInterpolation(in, out, null);
	}

	public static final List<CustomRegistryType<ByteBuf, Interpolation>> EASING = List.of(
		SINE,
		QUAD,
		CUBIC,
		QUART,
		QUINT,
		EXPO,
		CIRC,
		BACK,
		ELASTIC,
		BOUNCE
	);

	public static final CustomRegistryType<ByteBuf, Interpolation> TYPE = Interpolation.REGISTRY.dynamic(ID.klib("composite"),
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Interpolation.CODEC.fieldOf("in").forGetter(CompositeInterpolation::in),
			Interpolation.CODEC.fieldOf("out").forGetter(CompositeInterpolation::out)
		).apply(instance, CompositeInterpolation::of)),
		CompositeStreamCodec.of(
			Interpolation.STREAM_CODEC, CompositeInterpolation::in,
			Interpolation.STREAM_CODEC, CompositeInterpolation::out,
			CompositeInterpolation::of
		)
	);

	@Override
	public CustomRegistryType<ByteBuf, Interpolation> type() {
		return type == null ? TYPE : type;
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
