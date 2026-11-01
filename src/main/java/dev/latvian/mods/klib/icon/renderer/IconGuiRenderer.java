package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.icon.Icon;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.util.Cast;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IconGuiRenderer<T extends Icon> {
	static void drawIcon(@Nullable Ref<Icon> icon, GuiExtractContext ctx) {
		if (icon == null || icon == Icon.EMPTY) {
			return;
		}

		if (icon.value().type() instanceof DynamicType<?, ?> t && t.getProperty(Icon.PROPERTY_GUI_RENDERER) instanceof IconGuiRenderer<?> renderer) {
			renderer.draw(Cast.to(icon.value()), ctx);
		}
	}

	void draw(T icon, GuiExtractContext ctx);
}
