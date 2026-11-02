package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.icon.SimpleColorIcon;
import dev.latvian.mods.klib.render.SubmitContext;
import dev.latvian.mods.klib.util.client.EntityRenderTypes;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class SimpleColorIconRenderState extends IconRenderState {
	public static void draw(SimpleColorIcon icon, IconGuiExtractContext ctx) {
		var argb = icon.color().mixAlpha(ctx.alpha()).argb();

		if (ARGB.alpha(argb) == 0) {
			return;
		}

		ctx.graphics().fill(ctx.x() - 8, ctx.y() - 8, ctx.x() + 8, ctx.y() + 8, argb);
	}

	public int argb;
	public boolean translucent;

	@Nullable
	public static IconRenderState extract(SimpleColorIcon icon, SimpleColorIconRenderState state, IconWorldExactContext ctx) {
		state.argb = icon.color().mixAlpha(ctx.alpha()).argb();

		if (ARGB.alpha(state.argb) == 0) {
			return null;
		}

		state.translucent = ARGB.alpha(state.argb) < 255;
		return state;
	}

	@Override
	public void submit(SubmitContext ctx) {
		ctx.push(x, y, z);
		ctx.poseStack().rotate(rotation);

		ctx.submit(EntityRenderTypes.whiteCull(translucent), (pose, buffer) -> {
			var n = pose.transformNormal(0F, 1F, 0F, new Vector3f());
			buffer.addVertex(pose, -0.5F, -0.5F, 0F).setColor(argb).setUv(0F, 1F).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, 0.5F, -0.5F, 0F).setColor(argb).setUv(1F, 1F).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, 0.5F, 0.5F, 0F).setColor(argb).setUv(1F, 0F).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, -0.5F, 0.5F, 0F).setColor(argb).setUv(0F, 0F).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
		});

		ctx.pop();
	}
}
