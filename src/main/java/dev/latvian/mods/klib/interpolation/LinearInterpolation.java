package dev.latvian.mods.klib.interpolation;

import dev.latvian.mods.klib.registry.UnitType;
import io.netty.buffer.ByteBuf;

public enum LinearInterpolation implements Interpolation {
	INSTANCE;

	public static final UnitType<ByteBuf, Interpolation> TYPE = UnitType.create("linear", INSTANCE);

	@Override
	public UnitType<ByteBuf, Interpolation> type() {
		return TYPE;
	}

	@Override
	public float interpolate(float x) {
		return x;
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
