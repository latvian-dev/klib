package dev.latvian.mods.klib.entity.number;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class EntityNumberTypeRegistryEvent extends CustomRegistryTypeEvent<RegistryFriendlyByteBuf, EntityNumber> {
	public EntityNumberTypeRegistryEvent(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, EntityNumber> registry) {
		super(registry);
	}
}
