package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.math.Identity;
import dev.latvian.mods.klib.render.Submittable;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import org.joml.Quaternionfc;

public abstract class IconRenderState implements Submittable {
	public double x;
	public double y;
	public double z;
	public Quaternionfc rotation = Identity.QUATERNION;
	public int light = LightCoordsUtil.FULL_BRIGHT;
	public int overlay = OverlayTexture.NO_OVERLAY;
}
