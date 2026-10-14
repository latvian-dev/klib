package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public record EntityTypeTagFilter(TagKey<EntityType<?>> tag) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"type_tag",
		"tag",
		TagKey.codec(Registries.ENTITY_TYPE),
		TagKey.streamCodec(Registries.ENTITY_TYPE),
		EntityTypeTagFilter::new,
		EntityTypeTagFilter::tag
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		return entity.getType().builtInRegistryHolder().is(tag);
	}
}
