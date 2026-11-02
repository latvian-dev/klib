package dev.latvian.mods.klib.visual.renderer;

import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.util.Cast;
import dev.latvian.mods.klib.visual.Visual;
import org.jetbrains.annotations.Nullable;

public record VisualRenderer<V extends Visual, S extends VisualRenderState>(StateFactory<S> stateFactory, StateExtractor<V, S> stateExtractor) {
	@FunctionalInterface
	public interface StateFactory<S extends VisualRenderState> {
		S create();
	}

	@FunctionalInterface
	public interface StateExtractor<V extends Visual, S extends VisualRenderState> {
		@Nullable
		VisualRenderState extract(V visual, S state, VisualExactContext ctx);
	}

	@Nullable
	public static VisualRenderState extract(@Nullable Ref<Visual> visual, VisualExactContext ctx) {
		if (visual == null) {
			return null;
		} else if (visual.value().type() instanceof DynamicType<?, ?> t && t.getProperty(Visual.PROPERTY_RENDERER) instanceof VisualRenderer<?, ?> renderer) {
			return renderer.extractState(Cast.to(visual.value()), ctx);
		} else {
			return null;
		}
	}

	@Nullable
	private VisualRenderState extractState(V icon, VisualExactContext ctx) {
		return stateExtractor.extract(icon, stateFactory.create(), ctx);
	}
}
