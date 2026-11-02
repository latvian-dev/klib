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
		var shape = visual.shape().shape().shape().value();

		if (shape == EmptyShape.INSTANCE) {
			return null;
		}

		state.position = visual.shape().position().value().get(ctx.numberContext());

		if (state.position == null) {
			return null;
		}

		float progress = (float) visual.progress().value().getOr(ctx.numberContext(), 1D);
		var quadColor = visual.shape().shape().quads().value().get(progress);
		var lineColor = visual.shape().shape().lines().value().get(progress);

		if (quadColor.isTransparent() && lineColor.isTransparent()) {
			return null;
		}

		state.shape = shape;
		state.quadColor = quadColor;
		state.lineColor = lineColor;
		state.outerLineColor = visual.outerLines() ? state.lineColor : Color.TRANSPARENT;
		return state;
	}

	@Override
	public void submit(SubmitContext ctx) {
		ctx.push(position);
		ctx.submitQuads(shape, quadColor);
		ctx.submitLines(shape, lineColor);
		ctx.submitOuterLines(shape, outerLineColor);
		ctx.pop();
	}
}
