package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.function.Consumer;

public class CustomRegistryTypeEvent<B extends ByteBuf, T> extends Event implements IModBusEvent {
	private final CustomRegistryTypeCollector<B, T> callback;

	public CustomRegistryTypeEvent(CustomRegistryTypeCollector<B, T> callback) {
		this.callback = callback;
	}

	public void register(CustomRegistryType<B, T> type) {
		callback.register(type);
	}

	public void registerAll(Consumer<CustomRegistryTypeCollector<B, T>> consumer) {
		consumer.accept(callback);
	}
}
