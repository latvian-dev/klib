package dev.latvian.mods.klib.icon.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.klib.icon.TextureIcon;
import dev.latvian.mods.klib.texture.UV;
import dev.latvian.mods.klib.util.client.EntityRenderTypes;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.ClientAsset;
import net.minecraft.util.ARGB;
import org.joml.Vector3f;

public class TextureIconRenderState extends IconRenderState<TextureIcon> {
	public static void draw(TextureIcon icon, GuiExtractContext ctx) {
		var texture = icon.texture().texturePath();
		var uv = icon.uv();
		var color = icon.color().mixAlpha(ctx.alpha()).argb();
		ctx.graphics().innerBlit(RenderPipelines.GUI_TEXTURED, texture, ctx.x() - 8, ctx.y() + 8, ctx.x() - 8, ctx.y() + 8, uv.u0(), uv.u1(), uv.v0(), uv.v1(), color);
	}

	public ClientAsset.ResourceTexture texture;
	public UV uv;
	public int color = 0xFFFFFFFF;
	public boolean translucent;

	@Override
	public void extract(TextureIcon icon, WorldExactContext ctx) {
		texture = icon.texture();
		uv = icon.uv();
		color = icon.color().mixAlpha(ctx.alpha()).argb();
		translucent = ARGB.alpha(color) < 255 || icon.translucent();
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector) {
		if (ARGB.alpha(color) == 0) {
			return;
		}

		collector.submitCustomGeometry(poseStack, EntityRenderTypes.textureCull(texture, translucent), (pose, buffer) -> {
			var n = pose.transformNormal(0F, 1F, 0F, new Vector3f());
			buffer.addVertex(pose, -0.5F, -0.5F, 0F).setColor(color).setUv(uv.u0(), uv.v1()).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, 0.5F, -0.5F, 0F).setColor(color).setUv(uv.u1(), uv.v1()).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, 0.5F, 0.5F, 0F).setColor(color).setUv(uv.u1(), uv.v0()).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, -0.5F, 0.5F, 0F).setColor(color).setUv(uv.u0(), uv.v0()).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
		});
	}
}
