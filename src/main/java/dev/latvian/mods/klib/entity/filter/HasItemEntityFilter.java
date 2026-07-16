package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.entity.EntityUtils;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.Ingredient;

public record HasItemEntityFilter(Ingredient item) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"has_item",
		"item",
		Ingredient.CODEC,
		Ingredient.CONTENTS_STREAM_CODEC,
		HasItemEntityFilter::new,
		HasItemEntityFilter::item
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		return EntityUtils.hasItem(entity, item);
	}
}
