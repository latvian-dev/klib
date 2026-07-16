package dev.latvian.mods.klib.kvector;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class KVectorTypeRegistryEvent extends CustomRegistryTypeEvent<RegistryFriendlyByteBuf, KVector> {
	public KVectorTypeRegistryEvent(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, KVector> registry) {
		super(registry);
	}
}
