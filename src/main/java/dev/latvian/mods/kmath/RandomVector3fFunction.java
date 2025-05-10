package dev.latvian.mods.kmath;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.joml.Vector3f;

import java.util.function.Consumer;

public record RandomVector3fFunction(RandomSource random, float range) implements Consumer<Vector3f> {
	public RandomVector3fFunction(long seed, float range) {
		this(new XoroshiroRandomSource(seed), range);
	}

	public RandomVector3fFunction(long seedLo, long seedHi, float range) {
		this(new XoroshiroRandomSource(seedLo, seedHi), range);
	}

	@Override
	public void accept(Vector3f v) {
		v.x += random.nextRange(range);
		v.y += random.nextRange(range);
		v.z += random.nextRange(range);
	}
}
