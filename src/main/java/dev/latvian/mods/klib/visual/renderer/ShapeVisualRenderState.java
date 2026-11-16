package dev.latvian.mods.klib.visual.renderer;

import dev.latvian.mods.klib.render.SubmitContext;
import dev.latvian.mods.klib.shape.EmptyShape;
import dev.latvian.mods.klib.shape.Shape;
import dev.latvian.mods.klib.visual.ShapeVisual;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ShapeVisualRenderState implements VisualRenderState {
	public Vec3 position;
	public Shape shape;
	public int quadColor;
	public int lineColor;
	public int outerLineColor;

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

		float progress = visual.progress().value().getFloatOrNaN(ctx.numberContext());

		if (Float.isNaN(progress)) {
			return null;
		}

		state.quadColor = visual.shape().shape().quads().value().get(progress).argb();
		state.lineColor = visual.shape().shape().lines().value().get(progress).argb();
		state.outerLineColor = visual.outerLines().value().get(progress).argb();

		if (ARGB.alpha(state.quadColor) == 0 && ARGB.alpha(state.lineColor) == 0 && ARGB.alpha(state.outerLineColor) == 0) {
			return null;
		}

		return state;
	}

	@Override
	public void submit(SubmitContext ctx) {
		var offset = ctx.offset(position);
		ctx.submitQuads(shape, offset, quadColor);
		ctx.submitLines(shape, offset, lineColor);
		ctx.submitOuterLines(shape, offset, outerLineColor);
	}
}
