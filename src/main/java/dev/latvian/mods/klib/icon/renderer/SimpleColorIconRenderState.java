package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.icon.SimpleColorIcon;
import dev.latvian.mods.klib.util.client.EntityRenderTypes;
import org.joml.Vector3f;

public class SimpleColorIconRenderState extends IconRenderState<SimpleColorIcon> {
	public Color color;

	@Override
	public void extract(SimpleColorIcon icon, float delta) {
		color = icon.color();
	}

	@Override
	public void draw(GuiContext ctx) {
		ctx.graphics().fill(-8, -8, 8, 8, color.mixAlpha(ctx.alpha()).argb());
	}

	@Override
	public void submit(WorldContext ctx) {
		ctx.submitNodeCollector().submitCustomGeometry(ctx.poseStack(), EntityRenderTypes.whiteCull(color.alpha() < 255), (pose, buffer) -> {
			int colR = color.red();
			int colG = color.green();
			int colB = color.blue();
			int colA = color.alpha();

			var n = pose.transformNormal(0F, 1F, 0F, new Vector3f());

			buffer.addVertex(pose, -0.5F, -0.5F, 0F).setColor(colR, colG, colB, colA).setUv(0F, 1F).setLight(ctx.light()).setOverlay(ctx.overlay()).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, 0.5F, -0.5F, 0F).setColor(colR, colG, colB, colA).setUv(1F, 1F).setLight(ctx.light()).setOverlay(ctx.overlay()).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, 0.5F, 0.5F, 0F).setColor(colR, colG, colB, colA).setUv(1F, 0F).setLight(ctx.light()).setOverlay(ctx.overlay()).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, -0.5F, 0.5F, 0F).setColor(colR, colG, colB, colA).setUv(0F, 0F).setLight(ctx.light()).setOverlay(ctx.overlay()).setNormal(n.x, n.y, n.z);
		});
	}
}
