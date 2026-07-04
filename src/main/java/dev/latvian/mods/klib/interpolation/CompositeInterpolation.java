package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.util.ID;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record CompositeInterpolation(Ref<Interpolation> in, Ref<Interpolation> out, @Nullable CustomRegistryType<ByteBuf, Interpolation> type) implements Interpolation {
	private static CustomRegistryType.Unit<ByteBuf, Interpolation> inOut(String id, EaseIn in, EaseOut out) {
		return Interpolation.REGISTRY.unitWithType(ID.klib(id), type -> new CompositeInterpolation(in.type.unit(), out.type.unit(), type));
	}

	public static final CustomRegistryType.Unit<ByteBuf, Interpolation> SINE = inOut("sine_in_out", EaseIn.SINE, EaseOut.SINE);
	public static final CustomRegistryType.Unit<ByteBuf, Interpolation> QUAD = inOut("quad_in_out", EaseIn.QUAD, EaseOut.QUAD);
	public static final CustomRegistryType.Unit<ByteBuf, Interpolation> CUBIC = inOut("cubic_in_out", EaseIn.CUBIC, EaseOut.CUBIC);
	public static final CustomRegistryType.Unit<ByteBuf, Interpolation> QUART = inOut("quart_in_out", EaseIn.QUART, EaseOut.QUART);
	public static final CustomRegistryType.Unit<ByteBuf, Interpolation> QUINT = inOut("quint_in_out", EaseIn.QUINT, EaseOut.QUINT);
	public static final CustomRegistryType.Unit<ByteBuf, Interpolation> EXPO = inOut("expo_in_out", EaseIn.EXPO, EaseOut.EXPO);
	public static final CustomRegistryType.Unit<ByteBuf, Interpolation> CIRC = inOut("circ_in_out", EaseIn.CIRC, EaseOut.CIRC);
	public static final CustomRegistryType.Unit<ByteBuf, Interpolation> BACK = inOut("back_in_out", EaseIn.BACK, EaseOut.BACK);
	public static final CustomRegistryType.Unit<ByteBuf, Interpolation> ELASTIC = inOut("elastic_in_out", EaseIn.ELASTIC, EaseOut.ELASTIC);
	public static final CustomRegistryType.Unit<ByteBuf, Interpolation> BOUNCE = inOut("bounce_in_out", EaseIn.BOUNCE, EaseOut.BOUNCE);

	public static CompositeInterpolation of(Ref<Interpolation> inRef, Ref<Interpolation> outRef) {
		return new CompositeInterpolation(inRef, outRef, null);
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
		return (t < 0.5D ? in.value().interpolate(t * 2D) : (1D + out.value().interpolate(t * 2D - 1D))) / 2D;
	}

	@Override
	public float interpolate(float t) {
		return (t < 0.5F ? in.value().interpolate(t * 2F) : (1F + out.value().interpolate(t * 2F - 1F))) / 2F;
	}

	@Override
	public @NotNull String toString() {
		return "Composite[" + in + " -> " + out + "]";
	}

	@Override
	public Interpolation optimize() {
		if (in == EaseIn.SINE.type && out == EaseOut.SINE.type) {
			return SINE.value();
		} else if (in == EaseIn.QUAD.type && out == EaseOut.QUAD.type) {
			return QUAD.value();
		} else if (in == EaseIn.CUBIC.type && out == EaseOut.CUBIC.type) {
			return CUBIC.value();
		} else if (in == EaseIn.QUART.type && out == EaseOut.QUART.type) {
			return QUART.value();
		} else if (in == EaseIn.QUINT.type && out == EaseOut.QUINT.type) {
			return QUINT.value();
		} else if (in == EaseIn.EXPO.type && out == EaseOut.EXPO.type) {
			return EXPO.value();
		} else if (in == EaseIn.CIRC.type && out == EaseOut.CIRC.type) {
			return CIRC.value();
		} else if (in == EaseIn.BACK.type && out == EaseOut.BACK.type) {
			return BACK.value();
		} else if (in == EaseIn.ELASTIC.type && out == EaseOut.ELASTIC.type) {
			return ELASTIC.value();
		} else if (in == EaseIn.BOUNCE.type && out == EaseOut.BOUNCE.type) {
			return BOUNCE.value();
		} else {
			return this;
		}
	}
}
