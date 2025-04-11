package dev.latvian.mods.kmath.easing;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum EasingGroup implements StringRepresentable {
	MIN("min", Easing.MIN),
	MAX("max", Easing.MAX),
	HALF("half", Easing.HALF),
	LINEAR("linear", Easing.LINEAR),
	SMOOTHSTEP("smoothstep", Easing.SMOOTHSTEP),
	ISMOOTHSTEP("ismoothstep", Easing.ISMOOTHSTEP),
	SMOOTHERSTEP("smootherstep", Easing.SMOOTHERSTEP),
	SINE("sine", Easing.SINE_IN, Easing.SINE_OUT, Easing.SINE_IN_OUT),
	QUAD("quad", Easing.QUAD_IN, Easing.QUAD_OUT, Easing.QUAD_IN_OUT),
	CUBIC("cubic", Easing.CUBIC_IN, Easing.CUBIC_OUT, Easing.CUBIC_IN_OUT),
	QUART("quart", Easing.QUART_IN, Easing.QUART_OUT, Easing.QUART_IN_OUT),
	QUINT("quint", Easing.QUINT_IN, Easing.QUINT_OUT, Easing.QUINT_IN_OUT),
	EXPO("expo", Easing.EXPO_IN, Easing.EXPO_OUT, Easing.EXPO_IN_OUT),
	CIRC("circ", Easing.CIRC_IN, Easing.CIRC_OUT, Easing.CIRC_IN_OUT),
	BACK("back", Easing.BACK_IN, Easing.BACK_OUT, Easing.BACK_IN_OUT),
	ELASTIC("elastic", Easing.ELASTIC_IN, Easing.ELASTIC_OUT, Easing.ELASTIC_IN_OUT),
	BOUNCE("bounce", Easing.BOUNCE_IN, Easing.BOUNCE_OUT, Easing.BOUNCE_IN_OUT),

	;

	public static final EasingGroup[] VALUES = values();
	public static final Codec<EasingGroup> CODEC = StringRepresentable.fromEnum(() -> VALUES);
	public static final StreamCodec<ByteBuf, EasingGroup> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(i -> VALUES[i], EasingGroup::ordinal);

	public final String name;
	public final Easing in;
	public final Easing out;
	public final Easing inOut;

	EasingGroup(String name, Easing in, Easing out, Easing inOut) {
		this.name = name;
		this.in = in;
		this.out = out;
		this.inOut = inOut;
	}

	EasingGroup(String name, Easing easing) {
		this(name, easing, easing, easing);
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public double easeMirrored(double x, EasingGroup end) {
		return x < 0.5D ? out.ease(x * 2D) : 1D - end.out.ease((x - 0.5D) * 2D);
	}

	public double easeMirrored(double x) {
		return easeMirrored(x, this);
	}
}