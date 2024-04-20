package dev.latvian.mods.kmath.pos;

import org.joml.Vector3d;

public record KFixedPos(Number x, Number y, Number z) implements KPos {
	public static final KFixedPos ZERO = new KFixedPos(0D, 0D, 0D);
	public static final KFixedPos ONE = new KFixedPos(1D, 1D, 1D);

	@Override
	public void tick(Vector3d pos) {
		pos.set(x.doubleValue(), y.doubleValue(), z.doubleValue());
	}
}
