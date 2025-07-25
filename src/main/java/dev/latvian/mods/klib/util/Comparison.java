package dev.latvian.mods.klib.util;

import dev.latvian.mods.klib.data.DataType;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum Comparison implements StringRepresentable {
	EQUALS("equals", "=="),
	NOT_EQUALS("not_equals", "!="),
	GREATER_THAN("greater_than", ">"),
	GREATER_THAN_OR_EQUALS("greater_than_or_equals", ">="),
	LESS_THAN("less_than", "<"),
	LESS_THAN_OR_EQUALS("less_than_or_equals", "<=");

	public static final Comparison[] VALUES = values();
	public static final IntFunction<Comparison[]> ARRAY_FACTORY = Comparison[]::new;
	public static final DataType<Comparison> DATA_TYPE = DataType.of(VALUES);

	public final String name;
	public final String symbol;

	Comparison(String name, String symbol) {
		this.name = name;
		this.symbol = symbol;
	}

	public boolean test(double a, double b) {
		return switch (this) {
			case EQUALS -> Math.abs(a - b) < 0.00001;
			case NOT_EQUALS -> Math.abs(a - b) >= 0.00001;
			case GREATER_THAN -> a > b;
			case GREATER_THAN_OR_EQUALS -> a >= b;
			case LESS_THAN -> a < b;
			case LESS_THAN_OR_EQUALS -> a <= b;
		};
	}

	public boolean test(float a, float b) {
		return switch (this) {
			case EQUALS -> Math.abs(a - b) < 0.00001F;
			case NOT_EQUALS -> Math.abs(a - b) >= 0.00001F;
			case GREATER_THAN -> a > b;
			case GREATER_THAN_OR_EQUALS -> a >= b;
			case LESS_THAN -> a < b;
			case LESS_THAN_OR_EQUALS -> a <= b;
		};
	}

	public boolean test(int a, int b) {
		return switch (this) {
			case EQUALS -> a == b;
			case NOT_EQUALS -> a != b;
			case GREATER_THAN -> a > b;
			case GREATER_THAN_OR_EQUALS -> a >= b;
			case LESS_THAN -> a < b;
			case LESS_THAN_OR_EQUALS -> a <= b;
		};
	}

	public boolean test(long a, long b) {
		return switch (this) {
			case EQUALS -> a == b;
			case NOT_EQUALS -> a != b;
			case GREATER_THAN -> a > b;
			case GREATER_THAN_OR_EQUALS -> a >= b;
			case LESS_THAN -> a < b;
			case LESS_THAN_OR_EQUALS -> a <= b;
		};
	}

	@Override
	public String getSerializedName() {
		return symbol;
	}
}
