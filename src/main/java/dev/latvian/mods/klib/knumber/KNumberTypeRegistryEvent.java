package dev.latvian.mods.klib.knumber;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class KNumberTypeRegistryEvent extends CustomRegistryTypeEvent<RegistryFriendlyByteBuf, KNumber> {
	public KNumberTypeRegistryEvent(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, KNumber> registry) {
		super(registry);
	}
}
