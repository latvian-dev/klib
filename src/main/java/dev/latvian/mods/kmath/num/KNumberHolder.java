package dev.latvian.mods.kmath.num;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kmath.KStore;
import dev.latvian.mods.kmath.util.DeltaTicking;
import net.minecraft.util.math.MathHelper;

public class KNumberHolder implements DeltaTicking {
	public static Number fromJson(KStore parent, JsonElement json) {
		if (json instanceof JsonPrimitive p) {
			if (p.isNumber()) {
				return p.getAsNumber();
			} else if (p.isString()) {
				var str = p.getAsString();

				return switch (str) {
					case "0", "false" -> KNumber.ZERO;
					case "1", "true" -> KNumber.ONE;
					case "nan", "NaN" -> KNumber.NaN;
					case "+inf", "+Inf" -> KNumber.PInf;
					case "-inf", "-Inf" -> KNumber.NInf;
					default -> new KLazyNumber(str.trim());
				};
			} else if (p.isBoolean()) {
				return p.getAsBoolean() ? KNumber.ONE : KNumber.ZERO;
			}
		}

		throw new IllegalArgumentException("Invalid number JSON: " + json);
	}

	public final KStore parent;
	public Number source;
	public double value;
	public double prevValue;

	public KNumberHolder(KStore parent, Number source) {
		this.parent = parent;
		this.source = source;
		this.value = this.prevValue = source.doubleValue();
	}

	@Override
	public void snap() {
		prevValue = value;
	}

	@Override
	public void tickValue() {
		value = source.doubleValue();
	}

	public double get(double delta) {
		return MathHelper.lerp(delta, prevValue, value);
	}

	public void update(JsonElement json) {
		if (json != null) {
			source = fromJson(parent, json);
		}
	}

	public boolean getBoolean() {
		return value != 0;
	}

	public boolean changed() {
		return value != prevValue;
	}
}
