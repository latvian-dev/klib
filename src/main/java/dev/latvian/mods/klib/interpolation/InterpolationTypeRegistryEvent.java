package dev.latvian.mods.klib.interpolation;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import io.netty.buffer.ByteBuf;

public class InterpolationTypeRegistryEvent extends CustomRegistryTypeEvent<ByteBuf, Interpolation> {
	public InterpolationTypeRegistryEvent(CustomRegistryTypeCollector<ByteBuf, Interpolation> callback) {
		super(callback);
	}
}
