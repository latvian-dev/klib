package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.icon.AtlasSpriteIcon;
import dev.latvian.mods.klib.render.SubmitContext;
import dev.latvian.mods.klib.texture.UV;
import dev.latvian.mods.klib.util.client.EntityRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;

public class AtlasSpriteIconRenderState extends IconRenderState {
	public static void draw(AtlasSpriteIcon icon, IconGuiExtractContext ctx) {
		var uv = Minecraft.getInstance().getAtlasManager().get(new SpriteId(icon.sprite().atlas(), icon.sprite().sprite()));
		var color = icon.tint().mixAlpha(ctx.alpha()).argb();
		ctx.graphics().blitSprite(RenderPipelines.GUI_TEXTURED, uv, ctx.x() - 8, ctx.y() - 8, 16, 16, color);
	}

	public int argb;
	public Identifier texture;
	public UV uv;
	public boolean translucent;

	@Nullable
	public static IconRenderState extract(AtlasSpriteIcon icon, AtlasSpriteIconRenderState state, IconWorldExactContext ctx) {
		state.argb = icon.tint().mixAlpha(ctx.alpha()).argb();

		if (ARGB.alpha(state.argb) == 0) {
			return null;
		}

		var sprite = ctx.mc().getAtlasManager().get(new SpriteId(icon.sprite().atlas(), icon.sprite().sprite()));
		state.texture = sprite.atlasLocation();
		state.uv = new UV(sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1());
		state.translucent = ARGB.alpha(state.argb) < 255 || icon.translucent();
		return state;
	}

	@Override
	public void submit(SubmitContext ctx) {
		ctx.push(x, y, z);
		ctx.poseStack().rotate(rotation);

		ctx.submit(EntityRenderTypes.texture(texture, translucent), (pose, buffer) -> {
			buffer.addVertex(pose, -0.5F, -0.5F, 0F).setColor(argb).setUv(uv.u0(), uv.v1()).setLight(light).setOverlay(overlay).setNormal(0F, 1F, 0F);
			buffer.addVertex(pose, 0.5F, -0.5F, 0F).setColor(argb).setUv(uv.u1(), uv.v1()).setLight(light).setOverlay(overlay).setNormal(0F, 1F, 0F);
			buffer.addVertex(pose, 0.5F, 0.5F, 0F).setColor(argb).setUv(uv.u1(), uv.v0()).setLight(light).setOverlay(overlay).setNormal(0F, 1F, 0F);
			buffer.addVertex(pose, -0.5F, 0.5F, 0F).setColor(argb).setUv(uv.u0(), uv.v0()).setLight(light).setOverlay(overlay).setNormal(0F, 1F, 0F);
		});

		ctx.pop();
	}
}
