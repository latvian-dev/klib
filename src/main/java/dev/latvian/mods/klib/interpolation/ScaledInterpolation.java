package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
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

	public static final DynamicType<ByteBuf, Interpolation> TYPE = DynamicType.create(
		"scaled",
		"scale",
		Codec.FLOAT,
		ByteBufCodecs.FLOAT,
		ScaledInterpolation::new,
		ScaledInterpolation::scale
	);

	@Override
	public CustomRegistryType<ByteBuf, Interpolation> type() {
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
