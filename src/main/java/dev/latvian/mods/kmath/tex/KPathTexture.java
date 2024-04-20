package dev.latvian.mods.kmath.tex;

import net.minecraft.util.Identifier;

public record KPathTexture(Identifier path, Number[] uvs) implements KTexture {
	@Override
	public Identifier getPath() {
		return path;
	}

	@Override
	public Number[] getUVs() {
		return uvs;
	}
}
