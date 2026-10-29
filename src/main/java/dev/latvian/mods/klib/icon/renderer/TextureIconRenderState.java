package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.icon.TextureIcon;
import dev.latvian.mods.klib.texture.UV;
import dev.latvian.mods.klib.util.client.EntityRenderTypes;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.ClientAsset;
import org.joml.Vector3f;

public class TextureIconRenderState extends IconRenderState<TextureIcon> {
	public ClientAsset.ResourceTexture texture;
	public UV uv;
	public boolean translucent;
	public Color color;

	@Override
	public void extract(TextureIcon icon, float delta) {
		texture = icon.texture();
		uv = icon.uv();
		translucent = icon.translucent();
		color = icon.color();
	}

	@Override
	public void draw(GuiContext ctx) {
		ctx.graphics().innerBlit(RenderPipelines.GUI_TEXTURED, texture.texturePath(), -8, 8, -8, 8, uv.u0(), uv.u1(), uv.v0(), uv.v1(), color.mixAlpha(ctx.alpha()).argb());
	}

	@Override
	public void submit(WorldContext ctx) {
		ctx.submitNodeCollector().submitCustomGeometry(ctx.poseStack(), EntityRenderTypes.textureCull(texture, color.alpha() < 255 || translucent), (pose, buffer) -> {
			int colR = color.red();
			int colG = color.green();
			int colB = color.blue();
			int colA = color.alpha();

			var n = pose.transformNormal(0F, 1F, 0F, new Vector3f());
			buffer.addVertex(pose, -0.5F, -0.5F, 0F).setColor(colR, colG, colB, colA).setUv(uv.u0(), uv.v1()).setLight(ctx.light()).setOverlay(ctx.overlay()).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, 0.5F, -0.5F, 0F).setColor(colR, colG, colB, colA).setUv(uv.u1(), uv.v1()).setLight(ctx.light()).setOverlay(ctx.overlay()).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, 0.5F, 0.5F, 0F).setColor(colR, colG, colB, colA).setUv(uv.u1(), uv.v0()).setLight(ctx.light()).setOverlay(ctx.overlay()).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, -0.5F, 0.5F, 0F).setColor(colR, colG, colB, colA).setUv(uv.u0(), uv.v0()).setLight(ctx.light()).setOverlay(ctx.overlay()).setNormal(n.x, n.y, n.z);
		});
	}
}
