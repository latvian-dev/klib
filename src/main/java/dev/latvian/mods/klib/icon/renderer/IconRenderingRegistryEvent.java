package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.icon.Icon;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.bus.api.Event;

public class IconRenderingRegistryEvent extends Event {
	public <T extends Icon> void registerGui(DynamicType<RegistryFriendlyByteBuf, Icon> type, IconGuiRenderer<T> renderer) {
		type.setProperty(Icon.PROPERTY_GUI_RENDERER, renderer);
	}

	public <T extends Icon> void registerWorld(DynamicType<RegistryFriendlyByteBuf, Icon> type, IconRenderState.Factory<T> factory) {
		type.setProperty(Icon.PROPERTY_RENDER_STATE_FACTORY, factory);
	}
}
