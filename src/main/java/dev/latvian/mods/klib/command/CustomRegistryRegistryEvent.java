package dev.latvian.mods.klib.command;

import dev.latvian.mods.klib.registry.CustomRegistryCollector;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class CustomRegistryRegistryEvent extends Event implements IModBusEvent {
	private final CustomRegistryCollector registry;

	public CustomRegistryRegistryEvent(CustomRegistryCollector registry) {
		this.registry = registry;
	}

	public CustomRegistryCollector getRegistry() {
		return registry;
	}
}
