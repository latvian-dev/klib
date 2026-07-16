package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class EntityFilterTypeRegistryEvent extends CustomRegistryTypeEvent<RegistryFriendlyByteBuf, EntityFilter> {
	public EntityFilterTypeRegistryEvent(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, EntityFilter> registry) {
		super(registry);
	}
}
