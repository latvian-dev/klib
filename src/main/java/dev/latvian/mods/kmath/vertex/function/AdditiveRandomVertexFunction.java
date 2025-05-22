package dev.latvian.mods.kmath.vertex.function;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.joml.Vector3f;

public record AdditiveRandomVertexFunction(RandomSource random, float range) implements VertexFunction {
	public AdditiveRandomVertexFunction(long seed, float range) {
		this(new XoroshiroRandomSource(seed), range);
	}

	public AdditiveRandomVertexFunction(long seedLo, long seedHi, float range) {
		this(new XoroshiroRandomSource(seedLo, seedHi), range);
	}

	@Override
	public void accept(Vector3f v) {
		v.x += random.nextRange(range);
		v.y += random.nextRange(range);
		v.z += random.nextRange(range);
	}
}
