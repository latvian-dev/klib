package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.color.TransparencyLevel;
import dev.latvian.mods.klib.icon.TextureIcon;
import dev.latvian.mods.klib.render.SubmitContext;
import dev.latvian.mods.klib.texture.SpriteRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class TextureIconRenderState extends IconRenderState {
	public static void draw(TextureIcon icon, IconGuiExtractContext ctx) {
		var color = icon.color().mixAlpha(ctx.alpha()).argb();

		if (ARGB.alpha(color) == 0) {
			return;
		}

		var texture = icon.texture().texturePath();
		var uv = icon.uv();
		ctx.graphics().innerBlit(RenderPipelines.GUI_TEXTURED, texture, ctx.x() - 8, ctx.y() + 8, ctx.x() - 8, ctx.y() + 8, uv.u0(), uv.u1(), uv.v0(), uv.v1(), color);
	}

	public int color = 0xFFFFFFFF;
	public SpriteRenderState sprite = new SpriteRenderState();

	@Nullable
	public static IconRenderState extract(TextureIcon icon, TextureIconRenderState state, IconWorldExactContext ctx) {
		state.color = icon.color().mixAlpha(ctx.alpha()).argb();

		if (ARGB.alpha(state.color) == 0) {
			return null;
		}

		state.sprite.extract(icon.texture().texturePath());
		state.sprite.uv = icon.uv();

		if (icon.translucent()) {
			state.sprite.transparency = TransparencyLevel.TRANSLUCENT;
		}

		return state;
	}

	@Override
	public void submit(SubmitContext ctx) {
		ctx.push(x, y, z);
		ctx.rotate(rotation);
		ctx.scale(scale);

		ctx.submit(sprite.getEntityRenderType(true, ARGB.alpha(color)), (pose, buffer) -> {
			var n = pose.transformNormal(0F, 1F, 0F, new Vector3f());
			buffer.addVertex(pose, -0.5F, -0.5F, 0F).setColor(color).setUv(sprite.uv.u0(), sprite.uv.v1()).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, 0.5F, -0.5F, 0F).setColor(color).setUv(sprite.uv.u1(), sprite.uv.v1()).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, 0.5F, 0.5F, 0F).setColor(color).setUv(sprite.uv.u1(), sprite.uv.v0()).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
			buffer.addVertex(pose, -0.5F, 0.5F, 0F).setColor(color).setUv(sprite.uv.u0(), sprite.uv.v0()).setLight(light).setOverlay(overlay).setNormal(n.x, n.y, n.z);
		});

		ctx.pop();
	}
}
