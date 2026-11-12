package dev.latvian.mods.klib.visual.renderer;

import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.render.SubmitContext;
import dev.latvian.mods.klib.visual.LineVisual;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LineVisualRenderState implements VisualRenderState {
	public Vec3 startPos;
	public Vec3 endPos;
	public Color startColor;
	public Color endColor;

	@Nullable
	public static VisualRenderState extract(LineVisual visual, LineVisualRenderState state, VisualExactContext ctx) {
		state.startPos = visual.start().position().value().get(ctx.numberContext());

		if (state.startPos == null) {
			return null;
		}

		state.endPos = visual.end().position().value().get(ctx.numberContext());

		if (state.endPos == null) {
			return null;
		}

		float progress = visual.progress().value().getFloatOrNaN(ctx.numberContext());

		if (Float.isNaN(progress)) {
			return null;
		}

		var startColor0 = visual.start().color().value().get(0F);
		var startColor1 = visual.start().color().value().get(1F);
		state.startColor = startColor0.lerp(progress, startColor1);

		var endColor0 = visual.end().color().value().get(0F);
		var endColor1 = visual.end().color().value().get(1F);
		state.endColor = endColor0.lerp(progress, endColor1);

		if (state.startColor.isTransparent() && state.endColor.isTransparent()) {
			return null;
		}

		return state;
	}

	@Override
	public void submit(SubmitContext ctx) {
		ctx.push(startPos);

		ctx.submit(RenderTypes.linesTranslucentNoDepthWrite(), (pose, buffer) -> {
			float sizeX = (float) (endPos.x - startPos.x);
			float sizeY = (float) (endPos.y - startPos.y);
			float sizeZ = (float) (endPos.z - startPos.z);
			var callback = pose.transform(buffer);
			callback.acceptPos(0F, 0F, 0F).acceptCol(startColor).acceptLineWidth(ctx.lineWidth());
			callback.acceptPos(sizeX, sizeY, sizeZ).acceptCol(endColor).acceptLineWidth(ctx.lineWidth());
		});

		ctx.pop();
	}
}
