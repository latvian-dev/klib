package dev.latvian.mods.klib.visual;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class VisualTypeRegistryEvent extends CustomRegistryTypeEvent<RegistryFriendlyByteBuf, Visual> {
	public VisualTypeRegistryEvent(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, Visual> registry) {
		super(registry);
	}
}