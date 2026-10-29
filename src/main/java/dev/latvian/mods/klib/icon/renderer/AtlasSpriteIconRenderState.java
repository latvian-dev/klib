package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.icon.AtlasSpriteIcon;
import dev.latvian.mods.klib.util.client.EntityRenderTypes;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.model.sprite.SpriteId;

public class AtlasSpriteIconRenderState extends IconRenderState<AtlasSpriteIcon> {
	public SpriteId sprite;
	public boolean translucent;
	public Color tint;

	@Override
	public void extract(AtlasSpriteIcon icon, float delta) {
		sprite = new SpriteId(icon.sprite().atlas(), icon.sprite().sprite());
		translucent = icon.translucent();
		tint = icon.tint();
	}

	@Override
	public void draw(GuiContext ctx) {
		var color = tint.mixAlpha(ctx.alpha()).argb();
		var uv = ctx.mc().getAtlasManager().get(sprite);
		ctx.graphics().blitSprite(RenderPipelines.GUI_TEXTURED, uv, -8, -8, 16, 16, color);
	}

	@Override
	public void submit(WorldContext ctx) {
		ctx.submitNodeCollector().submitCustomGeometry(ctx.poseStack(), EntityRenderTypes.texture(sprite.atlasLocation(), tint.alpha() < 255 || translucent), (pose, buffer) -> {
			int colR = tint.red();
			int colG = tint.green();
			int colB = tint.blue();
			int colA = tint.alpha();

			var uv = ctx.mc().getAtlasManager().get(sprite);

			buffer.addVertex(pose, -0.5F, -0.5F, 0F).setColor(colR, colG, colB, colA).setUv(uv.getU0(), uv.getV1()).setLight(ctx.light()).setOverlay(ctx.overlay()).setNormal(0F, 1F, 0F);
			buffer.addVertex(pose, 0.5F, -0.5F, 0F).setColor(colR, colG, colB, colA).setUv(uv.getU1(), uv.getV1()).setLight(ctx.light()).setOverlay(ctx.overlay()).setNormal(0F, 1F, 0F);
			buffer.addVertex(pose, 0.5F, 0.5F, 0F).setColor(colR, colG, colB, colA).setUv(uv.getU1(), uv.getV0()).setLight(ctx.light()).setOverlay(ctx.overlay()).setNormal(0F, 1F, 0F);
			buffer.addVertex(pose, -0.5F, 0.5F, 0F).setColor(colR, colG, colB, colA).setUv(uv.getU0(), uv.getV0()).setLight(ctx.light()).setOverlay(ctx.overlay()).setNormal(0F, 1F, 0F);
		});
	}
}
