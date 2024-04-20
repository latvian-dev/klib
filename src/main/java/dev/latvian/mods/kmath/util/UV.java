package dev.latvian.mods.kmath.util;

import net.minecraft.util.math.MathHelper;

public record UV(float u0, float v0, float u1, float v1) {
	public UV mul(UV uv) {
		return new UV(
			MathHelper.lerp(uv.u0, u0, u1),
			MathHelper.lerp(uv.v0, v0, v1),
			MathHelper.lerp(uv.u1, u0, u1),
			MathHelper.lerp(uv.v1, v0, v1)
		);
	}

	@Override
	public String toString() {
		return "[" + u0 + "," + v0 + "," + u1 + "," + v1 + "]";
	}
}
