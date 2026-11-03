package dev.latvian.mods.klib.visual.renderer;

import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.render.SubmitContext;
import dev.latvian.mods.klib.shape.EmptyShape;
import dev.latvian.mods.klib.shape.Shape;
import dev.latvian.mods.klib.visual.ShapeVisual;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ShapeVisualRenderState implements VisualRenderState {
	public Vec3 position;
	public Shape shape;
	public Color quadColor;
	public Color lineColor;
	public Color outerLineColor;

	@Nullable
	public static VisualRenderState extract(ShapeVisual visual, ShapeVisualRenderState state, VisualExactContext ctx) {
		state.shape = visual.shape().shape().shape().value();

		if (state.shape == EmptyShape.INSTANCE) {
			return null;
		}

		state.position = visual.shape().position().value().get(ctx.numberContext());

		if (state.position == null) {
			return null;
		}

		float progress = (float) visual.progress().value().getOr(ctx.numberContext(), 1D);
		state.quadColor = visual.shape().shape().quads().value().get(progress);
		state.lineColor = visual.shape().shape().lines().value().get(progress);
		state.outerLineColor = visual.outerLines().value().get(progress);

		if (state.quadColor.isTransparent() && state.lineColor.isTransparent() && state.outerLineColor.isTransparent()) {
			return null;
		}

		return state;
	}

	@Override
	public void submit(SubmitContext ctx) {
		var offset = ctx.offset(position);
		ctx.submitQuads(offset, shape, quadColor);
		ctx.submitLines(offset, shape, lineColor);
		ctx.submitOuterLines(offset, shape, outerLineColor);
	}
}
