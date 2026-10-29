package dev.latvian.mods.klib.bulk;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class BulkLevelModificationRegistryEvent extends CustomRegistryTypeEvent<RegistryFriendlyByteBuf, BulkLevelModification> {
	public BulkLevelModificationRegistryEvent(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, BulkLevelModification> registry) {
		super(registry);
	}
}
