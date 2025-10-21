package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record FixedInterpolation(float value) implements Interpolation {
	public static final FixedInterpolation MIN = new FixedInterpolation(0F);
	public static final FixedInterpolation MAX = new FixedInterpolation(1F);
	public static final FixedInterpolation MIDDLE = new FixedInterpolation(0.5F);

	public static FixedInterpolation of(float value) {
		if (value == 0F) {
			return MIN;
		} else if (value == 1F) {
			return MAX;
		} else if (value == 0.5F) {
			return MIDDLE;
		} else {
			return new FixedInterpolation(value);
		}
	}

	public static final MapCodec<FixedInterpolation> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("value").forGetter(FixedInterpolation::value)
	).apply(instance, FixedInterpolation::of));

	public static final StreamCodec<ByteBuf, FixedInterpolation> STREAM_CODEC = CompositeStreamCodec.of(
		ByteBufCodecs.FLOAT, FixedInterpolation::value,
		FixedInterpolation::of
	);

	public static final InterpolationType<FixedInterpolation> TYPE = InterpolationType.of("fixed", MAP_CODEC, STREAM_CODEC);

	@Override
	public InterpolationType<?> type() {
		return TYPE;
	}

	@Override
	public double interpolate(double t) {
		return value;
	}

	@Override
	public float interpolate(float t) {
		return value;
	}

	@Override
	public @NotNull String toString() {
		return Float.toString(value);
	}

	@Override
	public boolean isLinear() {
		return value == 1F;
	}
}
