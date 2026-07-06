package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public record InverseInterpolation(Ref<Interpolation> interpolation) implements Interpolation {
	public static final DynamicType<ByteBuf, Interpolation> TYPE = DynamicType.create(
		"inverse",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Interpolation.CODEC.fieldOf("interpolation").forGetter(InverseInterpolation::interpolation)
		).apply(instance, InverseInterpolation::new)),
		CompositeStreamCodec.of(
			Interpolation.STREAM_CODEC, InverseInterpolation::interpolation,
			InverseInterpolation::new
		)
	);

	@Override
	public CustomRegistryType<ByteBuf, Interpolation> type() {
		return TYPE;
	}

	@Override
	public double interpolate(double t) {
		return 1D - interpolation.value().interpolate(1D - t);
	}

	@Override
	public float interpolate(float t) {
		return 1F - interpolation.value().interpolate(1F - t);
	}

	@Override
	public @NotNull String toString() {
		return "Inverse[" + interpolation + "]";
	}
}
