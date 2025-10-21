package dev.latvian.mods.klib.interpolation;

public enum LinearInterpolation implements Interpolation {
	INSTANCE;

	public static final InterpolationType<LinearInterpolation> TYPE = InterpolationType.unit("linear", INSTANCE);

	@Override
	public InterpolationType<?> type() {
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
