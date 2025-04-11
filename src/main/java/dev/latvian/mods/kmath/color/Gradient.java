package dev.latvian.mods.kmath.color;

import net.minecraft.util.RandomSource;

public interface Gradient {
	Color get(float delta);

	default Color sample(RandomSource random) {
		return get(random.nextFloat());
	}
}
