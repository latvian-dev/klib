package dev.latvian.mods.kmath.random;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public enum NullRandom implements RandomSource, PositionalRandomFactory {
	INSTANCE;

	@Override
	public RandomSource fork() {
		return this;
	}

	@Override
	public PositionalRandomFactory forkPositional() {
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
	public RandomSource fromHashOf(String seed) {
		return this;
	}

	@Override
	public RandomSource fromSeed(long seed) {
		return this;
	}

	@Override
	public RandomSource at(int x, int y, int z) {
		return this;
	}

	@Override
	public void parityConfigString(StringBuilder info) {
	}
}
