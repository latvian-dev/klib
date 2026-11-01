package dev.latvian.mods.klib.icon.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.klib.icon.Icon;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.util.Cast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.jetbrains.annotations.Nullable;

public abstract class IconRenderState<T extends Icon> {
	@FunctionalInterface
	public interface Factory<T extends Icon> {
		IconRenderState<T> create();
	}

	public record GuiContext(Minecraft mc, GuiGraphicsExtractor graphics, int alpha) {
	}

	public record WorldContext(Minecraft mc, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay) {
	}

	@Nullable
	public static IconRenderState<?> extractState(@Nullable Ref<Icon> icon, float delta) {
		if (icon == null || icon == Icon.EMPTY) {
			return null;
		} else if (icon.value().type() instanceof DynamicType<?, ?> t && t.getProperty(Icon.PROPERTY_RENDER_STATE_FACTORY) instanceof IconRenderState.Factory<?> factory) {
			var state = factory.create();
			state.extract(Cast.to(icon.value()), delta);
			return state;
		} else {
			return null;
		}
	}

	public abstract void extract(T icon, float delta);

	public abstract void draw(GuiContext ctx);

	public abstract void submit(WorldContext ctx);
}
