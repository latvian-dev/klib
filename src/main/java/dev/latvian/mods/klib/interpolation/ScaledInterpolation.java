package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ScaledInterpolation(float scale) implements Interpolation {
	public static final ScaledInterpolation LINEAR = new ScaledInterpolation(1F);
	public static final ScaledInterpolation HALF = new ScaledInterpolation(0.5F);
	public static final ScaledInterpolation DOUBLE = new ScaledInterpolation(2F);

	public static ScaledInterpolation of(float value) {
		if (value == 1F) {
			return LINEAR;
		} else if (value == 0.5F) {
			return HALF;
		} else if (value == 2F) {
			return DOUBLE;
		} else {
			return new ScaledInterpolation(value);
		}
	}

	public static final MapCodec<ScaledInterpolation> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("scale").forGetter(ScaledInterpolation::scale)
	).apply(instance, ScaledInterpolation::of));

	public static final StreamCodec<ByteBuf, ScaledInterpolation> STREAM_CODEC = CompositeStreamCodec.of(
		ByteBufCodecs.FLOAT, ScaledInterpolation::scale,
		ScaledInterpolation::of
	);

	public static final InterpolationType<ScaledInterpolation> TYPE = InterpolationType.of("scaled", MAP_CODEC, STREAM_CODEC);

	@Override
	public InterpolationType<?> type() {
		return TYPE;
	}

	@Override
	public double interpolate(double t) {
		return t * scale;
	}

	@Override
	public float interpolate(float t) {
		return t * scale;
	}

	@Override
	public @NotNull String toString() {
		return "Scaled[" + scale + "]";
	}
}
