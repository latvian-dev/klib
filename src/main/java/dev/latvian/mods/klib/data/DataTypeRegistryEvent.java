package dev.latvian.mods.klib.data;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class DataTypeRegistryEvent extends Event implements IModBusEvent {
	private final DataTypeRegistry registry;

	public DataTypeRegistryEvent(DataTypeRegistry registry) {
		this.registry = registry;
	}

	public DataTypeRegistry getRegistry() {
		return registry;
	}
}
