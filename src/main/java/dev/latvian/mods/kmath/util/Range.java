package dev.latvian.mods.kmath.util;

import dev.latvian.mods.kmath.KMath;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.random.Random;

public record Range(float min, float max) {
	public static final Range ZERO = new Range(0F, 0F);
	public static final Range ONE = new Range(1F, 1F);

	public static Range of(float min, float max) {
		return min == 0F && max == 0F ? ZERO : min == 1F && max == 1F ? ONE : Math.abs(max - min) < 0.0001F ? new Range(min, min) : new Range(Math.min(min, max), Math.max(min, max));
	}

	public static Range of(float value) {
		return of(value, value);
	}

	public static Range of(NbtElement nbt) {
		var fp = FloatPair.of(nbt);
		return fp == null ? null : of(fp.a(), fp.b());
	}

	public NbtElement toNbt() {
		return FloatPair.toNbt(min, max);
	}

	public float get(float delta) {
		return min == max ? min : delta * (max - min) + min;
	}

	public float get(Random random) {
		return min == max ? min : random.nextFloat() * (max - min) + min;
	}

	@Override
	public String toString() {
		return min == max ? KMath.format(min) : (KMath.format(min) + " - " + KMath.format(max));
	}
}
