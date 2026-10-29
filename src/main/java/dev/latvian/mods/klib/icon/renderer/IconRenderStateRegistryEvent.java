package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.icon.Icon;
import net.neoforged.bus.api.Event;

import java.util.function.BiConsumer;

public class IconRenderStateRegistryEvent extends Event {
	private final BiConsumer<Class<?>, IconRenderState.Factory<?>> callback;

	public IconRenderStateRegistryEvent(BiConsumer<Class<?>, IconRenderState.Factory<?>> callback) {
		this.callback = callback;
	}

	public <T extends Icon> void register(Class<T> iconClass, IconRenderState.Factory<T> factory) {
		callback.accept(iconClass, factory);
	}
}
