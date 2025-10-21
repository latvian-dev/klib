package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * <a href="https://easings.net/">Source</a>
 */
public enum EaseIn implements Interpolation, StringRepresentable {
	SINE("sine", x -> 1D - Math.cos(x * Math.PI / 2D)),
	QUAD("quad", x -> x * x),
	CUBIC("cubic", x -> x * x * x),
	QUART("quart", x -> x * x * x * x),
	QUINT("quint", x -> x * x * x * x * x),
	EXPO("expo", x -> x == 0D ? 0D : Math.pow(2D, 10D * x - 10D)),
	CIRC("circ", x -> 1D - Math.sqrt(1D - x * x)),
	BACK("back", x -> x * x * (2.70158D * x - 1.70158D)),
	ELASTIC("elastic", x -> Math.sin(13D * Math.PI / 2D * x) * Math.pow(2D, 10D * x - 10D)),
	BOUNCE("bounce", x -> 1D - EaseOut.BOUNCE.interpolate(1D - x));

	public static final EaseIn[] VALUES = values();
	public static final Codec<EaseIn> CODEC = StringRepresentable.fromEnum(() -> VALUES);
	public static final StreamCodec<ByteBuf, EaseIn> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(i -> VALUES[i], EaseIn::ordinal);

	public final String name;
	public final Double2DoubleFunction function;
	public final InterpolationType<EaseIn> type;

	EaseIn(String name, Double2DoubleFunction function) {
		this.name = name;
		this.function = function;
		this.type = InterpolationType.unit(name + "_in", this);
	}

	@Override
	public InterpolationType<?> type() {
		return type;
	}

	@Override
	public double interpolate(double x) {
		return function.applyAsDouble(x);
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Interpolation inverse() {
		return EaseOut.VALUES[ordinal()];
	}

	@Override
	public Interpolation join(Interpolation other) {
		if (other instanceof EaseOut o && ordinal() == o.ordinal()) {
			return JoinedInterpolation.EASING[ordinal()].unit();
		}

		return Interpolation.super.join(other);
	}
}
