package dev.latvian.mods.klib.texture;

import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.render.SubmitContext;
import dev.latvian.mods.klib.vertex.VertexCallback;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FaceTextureRenderState {
	public SpriteRenderState sprite;
	public Color tint;
	public boolean cull;
	public LightUV light = LightUV.FULL_BRIGHT;
	public OverlayUV overlay = OverlayUV.NORMAL;

	public Consumer<VertexCallback> builderFunction;

	@Nullable
	public static FaceTextureRenderState extract(Minecraft mc, @Nullable FaceTexture face, Consumer<VertexCallback> builderFunction) {
		if (face == null || face == FaceTexture.EMPTY || face.tint().alpha() == 0) {
			return null;
		}

		var state = new FaceTextureRenderState();
		state.sprite = new SpriteRenderState();
		state.sprite.extract(mc, face.sprite());
		state.tint = face.tint();
		state.cull = face.cull();
		state.builderFunction = builderFunction;
		return state;
	}

	public void submit(SubmitContext ctx) {
		ctx.submit(sprite.getEntityRenderType(cull, tint.alpha()), (pose, buffer) -> builderFunction.accept(pose.transform(buffer).withColor(tint).withTex(sprite.uv).withLight(light).withOverlay(overlay)));
	}
}
