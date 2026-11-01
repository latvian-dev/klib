package dev.latvian.mods.klib.icon.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.klib.icon.Icon;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.util.Cast;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import org.jetbrains.annotations.Nullable;

public abstract class IconRenderState<T extends Icon> {
	public double x;
	public double y;
	public double z;
	public int light = LightCoordsUtil.FULL_BRIGHT;
	public int overlay = OverlayTexture.NO_OVERLAY;

	@FunctionalInterface
	public interface Factory<T extends Icon> {
		IconRenderState<T> create();
	}

	@Nullable
	public static IconRenderState<?> extractIcon(@Nullable Ref<Icon> icon, WorldExactContext ctx) {
		if (icon == null || icon == Icon.EMPTY) {
			return null;
		} else if (icon.value().type() instanceof DynamicType<?, ?> t && t.getProperty(Icon.PROPERTY_RENDER_STATE_FACTORY) instanceof IconRenderState.Factory<?> factory) {
			var state = factory.create();
			state.extract(Cast.to(icon.value()), ctx);
			return state;
		} else {
			return null;
		}
	}

	public abstract void extract(T icon, WorldExactContext ctx);

	public abstract void submit(PoseStack poseStack, SubmitNodeCollector collector);
}
