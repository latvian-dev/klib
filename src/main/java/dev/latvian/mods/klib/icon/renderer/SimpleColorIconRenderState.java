package dev.latvian.mods.klib.icon.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.klib.icon.SimpleColorIcon;
import dev.latvian.mods.klib.util.client.EntityRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.ARGB;
import org.joml.Vector3f;

public class SimpleColorIconRenderState extends IconRenderState<SimpleColorIcon> {
	public static void draw(SimpleColorIcon icon, GuiExtractContext ctx) {
		var argb = icon.color().mixAlpha(ctx.alpha()).argb();
		ctx.graphics().fill(ctx.x() - 8, ctx.y() - 8, ctx.x() + 8, ctx.y() + 8, argb);
	}

	public int argb;
	public boolean translucent;

	@Override
	public void extract(SimpleColorIcon icon, WorldExactContext ctx) {
		argb = icon.color().mixAlpha(ctx.alpha()).argb();
		translucent = ARGB.alpha(argb) < 255;
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector) {
		if (ARGB.alpha(argb) == 0) {
			return;
		}

		collector.submitCustomGeometry(poseStack, EntityRenderTypes.whiteCull(translucent), (pose, buffer) -> {
			var n = pose.transformNormal(0F, 1F, 0F, new Vector3f());
			buffer.addVertex(pose, -0.5F, -0.5F, 0F).setColor(argb).setUv(0F, 1F).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, 0.5F, -0.5F, 0F).setColor(argb).setUv(1F, 1F).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, 0.5F, 0.5F, 0F).setColor(argb).setUv(1F, 0F).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, -0.5F, 0.5F, 0F).setColor(argb).setUv(0F, 0F).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
		});
	}
}
