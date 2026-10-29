package dev.latvian.mods.klib.icon;

import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record ItemIcon(ItemStack stack) implements Icon {
	public static final DynamicType<RegistryFriendlyByteBuf, Icon> TYPE = DynamicType.create(
		"item",
		"item",
		ItemStack.CODEC,
		ItemStack.STREAM_CODEC,
		ItemIcon::new,
		ItemIcon::stack
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, Icon> type() {
		return TYPE;
	}
}
