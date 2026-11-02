package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.render.Visual;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;

public abstract class IconRenderState implements Visual {
	public int light = LightCoordsUtil.FULL_BRIGHT;
	public int overlay = OverlayTexture.NO_OVERLAY;
}
