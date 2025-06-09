package dev.latvian.mods.klib.math;

import dev.latvian.mods.klib.shape.QuadrilaterallyFacedConvexHexahedra;
import dev.latvian.mods.klib.texture.UV;

import java.util.Arrays;

public record SplitBox(Split split, int index, float centerX, float centerY, float centerZ, UV[] uvs, QuadrilaterallyFacedConvexHexahedra shape) {
	@Override
	public String toString() {
		return "SplitBox#" + index + "@" + centerX + ";" + centerY + ";" + centerZ + "=" + Arrays.toString(uvs);
	}

	public int key() {
		return (split.id << 7) | index;
	}
}
