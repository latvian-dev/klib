package dev.latvian.mods.kmath.num;

public class KLazyNumber extends KNumber {
	private final String value;
	private Number number;

	public KLazyNumber(String value) {
		this.value = value;
	}

	@Override
	public double doubleValue() {
		if (number == null) {
			number = KNumber.parse(value);
		}

		return number.doubleValue();
	}
}
