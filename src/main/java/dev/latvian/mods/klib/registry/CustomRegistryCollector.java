package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@FunctionalInterface
public interface CustomRegistryCollector {
	record Entry<B extends ByteBuf, T>(CustomRegistry<B, T> registry, @Nullable Consumer<CustomRegistryTypeCollector<B, T>> callback) {
	}

	<B extends ByteBuf, T> void register(Entry<B, T> entry);

	default <B extends ByteBuf, T> void register(CustomRegistry<B, T> registry, @Nullable Consumer<CustomRegistryTypeCollector<B, T>> callback) {
		register(new Entry<>(registry, callback));
	}
}
