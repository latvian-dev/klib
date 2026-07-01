package dev.latvian.mods.klib.interpolation;

import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.util.ID;
import io.netty.buffer.ByteBuf;

public enum LinearInterpolation implements Interpolation {
	INSTANCE;

	public static final CustomRegistryType<ByteBuf, Interpolation> TYPE = Interpolation.REGISTRY.unit(ID.klib("linear"), INSTANCE);

	@Override
	public CustomRegistryType<ByteBuf, Interpolation> type() {
		return TYPE;
	}

	@Override
	public double interpolate(double t) {
		return t;
	}

	@Override
	public float interpolate(float t) {
		return t;
	}

	@Override
	public String toString() {
		return "linear";
	}

	@Override
	public boolean isLinear() {
		return true;
	}
}
