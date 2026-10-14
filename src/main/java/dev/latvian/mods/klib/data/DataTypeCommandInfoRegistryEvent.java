package dev.latvian.mods.klib.data;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class DataTypeCommandInfoRegistryEvent extends Event implements IModBusEvent {
	private final DataTypeCommandInfoRegistry registry;

	public DataTypeCommandInfoRegistryEvent(DataTypeCommandInfoRegistry registry) {
		this.registry = registry;
	}

	public DataTypeCommandInfoRegistry getRegistry() {
		return registry;
	}
}
