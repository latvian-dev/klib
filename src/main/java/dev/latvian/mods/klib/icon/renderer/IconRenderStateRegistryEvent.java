package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.icon.Icon;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.bus.api.Event;

public class IconRenderStateRegistryEvent extends Event {
	public <T extends Icon> void register(DynamicType<RegistryFriendlyByteBuf, Icon> type, IconRenderState.Factory<T> factory) {
		type.setProperty(Icon.PROPERTY_RENDER_STATE_FACTORY, factory);
	}
}
