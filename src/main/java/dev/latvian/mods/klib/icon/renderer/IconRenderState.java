package dev.latvian.mods.klib.icon.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.klib.icon.Icon;
import dev.latvian.mods.klib.util.Lazy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Map;

public abstract class IconRenderState<T extends Icon> {
	public static final Lazy<Map<Class<?>, IconRenderState.Factory<?>>> BY_CLASS = Lazy.identityMap(map -> NeoForge.EVENT_BUS.post(new IconRenderStateRegistryEvent(map::put)));

	@FunctionalInterface
	public interface Factory<T extends Icon> {
		IconRenderState<T> create();
	}

	public record GuiContext(Minecraft mc, GuiGraphicsExtractor graphics, int alpha) {
	}

	public record WorldContext(Minecraft mc, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int light, int overlay) {
	}

	public abstract void extract(T icon, float delta);

	public abstract void draw(GuiContext ctx);

	public abstract void submit(WorldContext ctx);
}
