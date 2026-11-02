package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.icon.AtlasSpriteIcon;
import dev.latvian.mods.klib.render.SubmitContext;
import dev.latvian.mods.klib.texture.SpriteRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;

public class AtlasSpriteIconRenderState extends IconRenderState {
	public static void draw(AtlasSpriteIcon icon, IconGuiExtractContext ctx) {
		var sprite = ctx.mc().getAtlasManager().get(new SpriteId(icon.sprite().atlas(), icon.sprite().sprite()));
		var color = icon.tint().mixAlpha(ctx.alpha()).argb();
		ctx.graphics().blitSprite(RenderPipelines.GUI_TEXTURED, sprite, ctx.x() - 8, ctx.y() - 8, 16, 16, color);
	}

	public int argb;
	public SpriteRenderState sprite = new SpriteRenderState();
	public boolean cull = true;

	@Nullable
	public static IconRenderState extract(AtlasSpriteIcon icon, AtlasSpriteIconRenderState state, IconWorldExactContext ctx) {
		state.argb = icon.tint().mixAlpha(ctx.alpha()).argb();

		if (ARGB.alpha(state.argb) == 0) {
			return null;
		}

		state.sprite.extract(ctx.mc(), icon.sprite());
		state.cull = icon.cull();
		return state;
	}

	@Override
	public void submit(SubmitContext ctx) {
		ctx.push(x, y, z);
		ctx.rotate(rotation);
		ctx.scale(scale);

		ctx.submit(sprite.getEntityRenderType(cull, ARGB.alpha(argb)), (pose, buffer) -> {
			buffer.addVertex(pose, -0.5F, -0.5F, 0F).setColor(argb).setUv(sprite.uv.u0(), sprite.uv.v1()).setLight(light).setOverlay(overlay).setNormal(0F, 1F, 0F);
			buffer.addVertex(pose, 0.5F, -0.5F, 0F).setColor(argb).setUv(sprite.uv.u1(), sprite.uv.v1()).setLight(light).setOverlay(overlay).setNormal(0F, 1F, 0F);
			buffer.addVertex(pose, 0.5F, 0.5F, 0F).setColor(argb).setUv(sprite.uv.u1(), sprite.uv.v0()).setLight(light).setOverlay(overlay).setNormal(0F, 1F, 0F);
			buffer.addVertex(pose, -0.5F, 0.5F, 0F).setColor(argb).setUv(sprite.uv.u0(), sprite.uv.v0()).setLight(light).setOverlay(overlay).setNormal(0F, 1F, 0F);
		});

		ctx.pop();
	}
}
