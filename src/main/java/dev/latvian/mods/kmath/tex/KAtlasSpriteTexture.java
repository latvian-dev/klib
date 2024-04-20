package dev.latvian.mods.kmath.tex;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public record KAtlasSpriteTexture(Identifier atlas, Identifier sprite, Number[] uvs) implements KTexture {
	public static final Identifier BLOCKS = new Identifier("minecraft", "textures/atlas/blocks.png");
	public static final Identifier PARTICLES = new Identifier("minecraft", "textures/atlas/particles.png");

	@Override
	public Identifier getPath() {
		return atlas;
	}

	@Override
	public Number[] getUVs() {
		var s = MinecraftClient.getInstance().getSpriteAtlas(atlas).apply(sprite);
		uvs[0] = s.getMinU();
		uvs[1] = s.getMinV();
		uvs[2] = s.getMaxU();
		uvs[3] = s.getMaxV();
		return uvs;
	}
}
