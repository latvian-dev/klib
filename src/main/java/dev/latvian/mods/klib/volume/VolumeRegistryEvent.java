package dev.latvian.mods.klib.volume;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import io.netty.buffer.ByteBuf;

public class VolumeRegistryEvent extends CustomRegistryTypeEvent<ByteBuf, Volume> {
	public VolumeRegistryEvent(CustomRegistryTypeCollector<ByteBuf, Volume> registry) {
		super(registry);
	}
}
