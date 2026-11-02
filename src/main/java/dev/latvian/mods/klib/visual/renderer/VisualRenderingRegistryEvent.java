package dev.latvian.mods.klib.visual.renderer;

import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.visual.Visual;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class VisualRenderingRegistryEvent extends Event implements IModBusEvent {
	public <V extends Visual, S extends VisualRenderState> void register(DynamicType<RegistryFriendlyByteBuf, Visual> type, VisualRenderer.StateFactory<S> stateFactory, VisualRenderer.StateExtractor<V, S> stateExtractor) {
		type.setProperty(Visual.PROPERTY_RENDERER, new VisualRenderer<>(stateFactory, stateExtractor));
	}
}
