package dev.latvian.mods.klib.vertex.function;

import dev.latvian.mods.klib.math.RandomUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.joml.Vector3f;

public record MultiplicativeRandomVertexFunction(RandomSource random, float mod) implements VertexFunction {
	public MultiplicativeRandomVertexFunction(long seed, float mod) {
		this(new XoroshiroRandomSource(seed), mod);
	}

	public MultiplicativeRandomVertexFunction(long seedLo, long seedHi, float mod) {
		this(new XoroshiroRandomSource(seedLo, seedHi), mod);
	}

	@Override
	public void accept(Vector3f v) {
		v.x *= 1F + RandomUtils.nextRange(random, mod);
		v.y *= 1F + RandomUtils.nextRange(random, mod);
		v.z *= 1F + RandomUtils.nextRange(random, mod);
	}
}
