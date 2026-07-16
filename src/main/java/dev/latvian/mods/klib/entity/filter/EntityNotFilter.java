package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public record EntityNotFilter(Ref<EntityFilter> filter) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"not",
		"filter",
		EntityFilter.CODEC,
		EntityFilter.STREAM_CODEC,
		EntityNotFilter::new,
		EntityNotFilter::filter
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		return !filter.value().test(entity);
	}

	@Override
	public EntityFilter not() {
		return filter.value();
	}
}
