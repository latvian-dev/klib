package dev.latvian.mods.klib.icon.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;

public abstract class IconRenderState {
	public int light = LightCoordsUtil.FULL_BRIGHT;
	public int overlay = OverlayTexture.NO_OVERLAY;

	public abstract void submit(PoseStack poseStack, SubmitNodeCollector collector);
}
