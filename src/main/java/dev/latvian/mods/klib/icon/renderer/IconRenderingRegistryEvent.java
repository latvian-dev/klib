package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.icon.Icon;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class IconRenderingRegistryEvent extends Event implements IModBusEvent {
	public <I extends Icon, S extends IconRenderState> void register(DynamicType<RegistryFriendlyByteBuf, Icon> type, IconRenderer.StateFactory<S> stateFactory, IconRenderer.StateExtractor<I, S> stateExtractor, IconRenderer.GuiExtractor<I> guiExtractor) {
		type.setProperty(Icon.PROPERTY_RENDERER, new IconRenderer<>(stateFactory, stateExtractor, guiExtractor));
	}
}
