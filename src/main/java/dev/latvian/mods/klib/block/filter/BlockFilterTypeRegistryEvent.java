package dev.latvian.mods.klib.block.filter;

import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class BlockFilterTypeRegistryEvent extends CustomRegistryTypeEvent<RegistryFriendlyByteBuf, BlockFilter> {
	public BlockFilterTypeRegistryEvent(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, BlockFilter> registry) {
		super(registry);
	}
}
