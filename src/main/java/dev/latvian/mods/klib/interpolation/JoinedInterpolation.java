package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public record JoinedInterpolation(Ref<Interpolation> left, Ref<Interpolation> right) implements Interpolation {
	public static final DynamicType<ByteBuf, Interpolation> TYPE = DynamicType.create(
		"joined",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Interpolation.CODEC.fieldOf("left").forGetter(JoinedInterpolation::left),
			Interpolation.CODEC.fieldOf("right").forGetter(JoinedInterpolation::right)
		).apply(instance, JoinedInterpolation::new)),
		CompositeStreamCodec.of(
			Interpolation.STREAM_CODEC, JoinedInterpolation::left,
			Interpolation.STREAM_CODEC, JoinedInterpolation::right,
			JoinedInterpolation::new
		)
	);

	@Override
	public DynamicType<ByteBuf, Interpolation> type() {
		return TYPE;
	}

	@Override
	public double interpolate(double t) {
		return t < 0.5D ? left.value().interpolate(t * 2D) : (right.value().interpolate(t * 2D - 1D));
	}

	@Override
	public float interpolate(float t) {
		return t < 0.5F ? left.value().interpolate(t * 2F) : (right.value().interpolate(t * 2F - 1F));
	}

	@Override
	public @NotNull String toString() {
		return "Joined[" + left + " -> " + right + "]";
	}
}
