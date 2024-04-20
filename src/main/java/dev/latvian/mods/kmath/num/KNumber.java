package dev.latvian.mods.kmath.num;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kmath.KStore;

public abstract class KNumber extends Number {
	public static final Number ZERO = 0D;
	public static final Number ONE = 1D;
	public static final Number ZEROF = 0F;
	public static final Number ONEF = 1F;
	public static final Number NaN = Double.NaN;
	public static final Number PInf = Double.POSITIVE_INFINITY;
	public static final Number NInf = Double.NEGATIVE_INFINITY;

	public static Number fromJson(KStore parent, JsonElement json) {
		if (json instanceof JsonPrimitive p) {
			if (p.isNumber()) {
				return p.getAsNumber();
			} else if (p.isString()) {
				var str = p.getAsString();

				return switch (str) {
					case "0", "false" -> ZERO;
					case "1", "true" -> ONE;
					case "nan", "NaN" -> NaN;
					case "+inf", "+Inf" -> PInf;
					case "-inf", "-Inf" -> NInf;
					default -> new KLazyNumber(str.trim());
				};
			} else if (p.isBoolean()) {
				return p.getAsBoolean() ? ONE : ZERO;
			}
		}

		throw new IllegalArgumentException("Invalid number JSON: " + json);
	}

	protected static Number parse(String value) {
		return Double.parseDouble(value);
	}

	@Override
	public int intValue() {
		return (int) doubleValue();
	}

	@Override
	public long longValue() {
		return (long) doubleValue();
	}

	@Override
	public float floatValue() {
		return (float) doubleValue();
	}
}
