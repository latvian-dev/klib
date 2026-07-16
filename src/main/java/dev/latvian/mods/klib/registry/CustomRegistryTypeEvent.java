package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class CustomRegistryTypeEvent<B extends ByteBuf, T> extends Event implements IModBusEvent {
	private final CustomRegistryTypeCollector<B, T> collector;

	public CustomRegistryTypeEvent(CustomRegistryTypeCollector<B, T> collector) {
		this.collector = collector;
	}

	public CustomRegistryTypeCollector<B, T> getCollector() {
		return collector;
	}
}
