package dev.latvian.mods.klib.texture;

import dev.latvian.mods.klib.color.TransparencyLevel;
import dev.latvian.mods.klib.util.client.EntityRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.resources.Identifier;

public class SpriteRenderState {
	public Identifier texture;
	public UV uv;
	public TransparencyLevel transparency;

	public void extract(Minecraft mc, SpriteId spriteId) {
		var s = mc.getAtlasManager().get(spriteId);
		texture = s.atlasLocation();
		uv = new UV(s.getU0(), s.getV0(), s.getU1(), s.getV1());
		transparency = TransparencyLevel.from(s.contents().transparency());
	}

	public void extract(Minecraft mc, AtlasSprite sprite) {
		extract(mc, new SpriteId(sprite.atlas(), sprite.sprite()));
	}

	public void extract(Minecraft mc, SpriteKey sprite) {
		if (sprite.isSpecial()) {
			extract(sprite.sprite());
		} else {
			extract(mc, new SpriteId(sprite.atlas(), sprite.sprite()));
		}
	}

	public void extract(Identifier tex) {
		texture = tex;
		uv = UV.FULL;
		transparency = null;
	}

	public void max(TransparencyLevel level) {
		transparency = transparency.max(level);
	}

	public TransparencyLevel getTransparencyLevel(TransparencyLevel base) {
		return transparency == null ? base : transparency.max(base);
	}

	public RenderType getEntityRenderType(boolean cull, TransparencyLevel base) {
		return EntityRenderTypes.texture(texture, getTransparencyLevel(base), cull);
	}

	public RenderType getEntityRenderType(boolean cull, int alpha) {
		return getEntityRenderType(cull, alpha < 255 ? TransparencyLevel.TRANSLUCENT : TransparencyLevel.TRANSPARENT);
	}
}
