package dev.latvian.mods.kmath.random;

import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

public class NullRandom implements Random, RandomSplitter {

	public static final NullRandom INSTANCE = new NullRandom();

	@Override
	public Random split() {
		return this;
	}

	@Override
	public RandomSplitter nextSplitter() {
		return this;
	}

	@Override
	public void setSeed(long seed) {
	}

	@Override
	public int nextInt() {
		return 0;
	}

	@Override
	public int nextInt(int bound) {
		return 0;
	}

	@Override
	public long nextLong() {
		return 0L;
	}

	@Override
	public boolean nextBoolean() {
		return false;
	}

	@Override
	public float nextFloat() {
		return 0F;
	}

	@Override
	public double nextDouble() {
		return 0D;
	}

	@Override
	public double nextGaussian() {
		return 0D;
	}

	@Override
	public Random split(String seed) {
		return this;
	}

	@Override
	public Random split(int x, int y, int z) {
		return this;
	}

	@Override
	public void addDebugInfo(StringBuilder info) {
	}
}
