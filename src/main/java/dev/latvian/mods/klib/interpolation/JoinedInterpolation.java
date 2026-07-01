package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.util.ID;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public record JoinedInterpolation(Interpolation left, Interpolation right) implements Interpolation {
	public static final CustomRegistryType<ByteBuf, Interpolation> TYPE = Interpolation.REGISTRY.dynamic(ID.klib("joined"),
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
	public CustomRegistryType<ByteBuf, Interpolation> type() {
		return TYPE;
	}

	@Override
	public double interpolate(double t) {
		return t < 0.5D ? left.interpolate(t * 2D) : (right.interpolate(t * 2D - 1D));
	}

	@Override
	public float interpolate(float t) {
		return t < 0.5F ? left.interpolate(t * 2F) : (right.interpolate(t * 2F - 1F));
	}

	@Override
	public @NotNull String toString() {
		return "Joined[" + left + " -> " + right + "]";
	}
}
