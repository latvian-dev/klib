package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.icon.Icon;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.util.Cast;
import org.jetbrains.annotations.Nullable;

public record IconRenderer<I extends Icon, S extends IconRenderState>(StateFactory<S> stateFactory, StateExtractor<I, S> stateExtractor, GuiExtractor<I> guiExtractor) {
	@FunctionalInterface
	public interface StateFactory<S extends IconRenderState> {
		S create();
	}

	@FunctionalInterface
	public interface StateExtractor<I extends Icon, S extends IconRenderState> {
		@Nullable
		IconRenderState extract(I icon, S state, IconWorldExactContext ctx);
	}

	@FunctionalInterface
	public interface GuiExtractor<I extends Icon> {
		void draw(I icon, IconGuiExtractContext ctx);
	}

	@Nullable
	public static IconRenderState extract(@Nullable Ref<Icon> icon, IconWorldExactContext ctx) {
		if (icon == null || icon == Icon.EMPTY || ctx.alpha() <= 0) {
			return null;
		} else if (icon.value().type() instanceof DynamicType<?, ?> t && t.getProperty(Icon.PROPERTY_RENDERER) instanceof IconRenderer<?, ?> renderer) {
			return renderer.extractState(Cast.to(icon.value()), ctx);
		} else {
			return null;
		}
	}

	public static void draw(@Nullable Ref<Icon> icon, IconGuiExtractContext ctx) {
		if (icon == null || icon == Icon.EMPTY || ctx.alpha() <= 0) {
			return;
		}

		if (icon.value().type() instanceof DynamicType<?, ?> t && t.getProperty(Icon.PROPERTY_RENDERER) instanceof IconRenderer<?, ?> renderer) {
			renderer.guiExtractor.draw(Cast.to(icon.value()), ctx);
		}
	}

	@Nullable
	private IconRenderState extractState(I icon, IconWorldExactContext ctx) {
		return stateExtractor.extract(icon, stateFactory.create(), ctx);
	}
}
