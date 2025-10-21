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
public enum EaseOut implements Interpolation, StringRepresentable {
	SINE("sine", x -> Math.sin(x * Math.PI / 2D)),
	QUAD("quad", x -> 1D - (1D - x) * (1D - x)),
	CUBIC("cubic", x -> 1D - Math.pow(1D - x, 3D)),
	QUART("quart", x -> 1D - Math.pow(1D - x, 4D)),
	QUINT("quint", x -> 1D - Math.pow(1D - x, 5D)),
	EXPO("expo", x -> x == 1D ? 1D : 1D - Math.pow(2D, -10D * x)),
	CIRC("circ", x -> Math.sqrt(1D - (x - 1D) * (x - 1D))),
	BACK("back", x -> 1D - (1D - x) * (1D - x) * (2.70158D * (1D - x) - 1.70158D)),
	ELASTIC("elastic", x -> Math.sin(-13D * Math.PI / 2D * (x + 1D)) * Math.pow(2D, -10D * x) + 1D),
	BOUNCE("bounce", x -> {
		if (x < 1D / 2.75D) {
			return 7.5625D * x * x;
		} else if (x < 2D / 2.75D) {
			return 7.5625D * (x -= 1.5D / 2.75D) * x + 0.75D;
		} else if (x < 2.5D / 2.75D) {
			return 7.5625D * (x -= 2.25D / 2.75D) * x + 0.9375D;
		} else {
			return 7.5625D * (x -= 2.625D / 2.75D) * x + 0.984375D;
		}
	});

	public static final EaseOut[] VALUES = values();
	public static final Codec<EaseOut> CODEC = StringRepresentable.fromEnum(() -> VALUES);
	public static final StreamCodec<ByteBuf, EaseOut> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(i -> VALUES[i], EaseOut::ordinal);

	public final String name;
	public final Double2DoubleFunction function;
	public final InterpolationType<EaseOut> type;

	EaseOut(String name, Double2DoubleFunction function) {
		this.name = name;
		this.function = function;
		this.type = InterpolationType.unit(name + "_out", this);
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
		return EaseIn.VALUES[ordinal()];
	}
}
