package dev.latvian.mods.klib.easing;

public record FixedValueEasingFunction(double value) implements EasingFunction {
	public static final FixedValueEasingFunction MIN = new FixedValueEasingFunction(0D);
	public static final FixedValueEasingFunction MAX = new FixedValueEasingFunction(1D);
	public static final FixedValueEasingFunction HALF = new FixedValueEasingFunction(0.5D);

	public static FixedValueEasingFunction of(double value) {
		if (value == 0D) {
			return MIN;
		} else if (value == 1D) {
			return MAX;
		} else if (value == 0.5D) {
			return HALF;
		} else {
			return new FixedValueEasingFunction(value);
		}
	}

	@Override
	public double ease(double x) {
		return value;
	}
}
