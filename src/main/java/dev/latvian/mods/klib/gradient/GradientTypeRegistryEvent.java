package dev.latvian.mods.klib.gradient;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import io.netty.buffer.ByteBuf;

public class GradientTypeRegistryEvent extends CustomRegistryTypeEvent<ByteBuf, Gradient> {
	public GradientTypeRegistryEvent(CustomRegistryTypeCollector<ByteBuf, Gradient> callback) {
		super(callback);
	}
}