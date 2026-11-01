package dev.latvian.mods.klib.icon.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.klib.icon.AtlasSpriteIcon;
import dev.latvian.mods.klib.texture.UV;
import dev.latvian.mods.klib.util.client.EntityRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class AtlasSpriteIconRenderState extends IconRenderState<AtlasSpriteIcon> {
	public static void draw(AtlasSpriteIcon icon, GuiExtractContext ctx) {
		var uv = Minecraft.getInstance().getAtlasManager().get(new SpriteId(icon.sprite().atlas(), icon.sprite().sprite()));
		var color = icon.tint().mixAlpha(ctx.alpha()).argb();
		ctx.graphics().blitSprite(RenderPipelines.GUI_TEXTURED, uv, ctx.x() - 8, ctx.y() - 8, 16, 16, color);
	}

	public Identifier texture;
	public UV uv;
	public int argb;
	public boolean translucent;

	@Override
	public void extract(AtlasSpriteIcon icon, WorldExactContext ctx) {
		var sprite = Minecraft.getInstance().getAtlasManager().get(new SpriteId(icon.sprite().atlas(), icon.sprite().sprite()));
		texture = sprite.atlasLocation();
		uv = new UV(sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1());
		argb = icon.tint().mixAlpha(ctx.alpha()).argb();
		translucent = ARGB.alpha(argb) < 255 || icon.translucent();
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector) {
		if (ARGB.alpha(argb) == 0) {
			return;
		}

		collector.submitCustomGeometry(poseStack, EntityRenderTypes.texture(texture, translucent), (pose, buffer) -> {
			buffer.addVertex(pose, -0.5F, -0.5F, 0F).setColor(argb).setUv(uv.u0(), uv.v1()).setLight(light).setOverlay(overlay).setNormal(0F, 1F, 0F);
			buffer.addVertex(pose, 0.5F, -0.5F, 0F).setColor(argb).setUv(uv.u1(), uv.v1()).setLight(light).setOverlay(overlay).setNormal(0F, 1F, 0F);
			buffer.addVertex(pose, 0.5F, 0.5F, 0F).setColor(argb).setUv(uv.u1(), uv.v0()).setLight(light).setOverlay(overlay).setNormal(0F, 1F, 0F);
			buffer.addVertex(pose, -0.5F, 0.5F, 0F).setColor(argb).setUv(uv.u0(), uv.v0()).setLight(light).setOverlay(overlay).setNormal(0F, 1F, 0F);
		});
	}
}
