package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public record EntityAndFilter(List<Ref<EntityFilter>> filters) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"and",
		"filters",
		EntityFilter.CODEC.listOf(),
		KLibStreamCodecs.listOf(EntityFilter.STREAM_CODEC),
		EntityAndFilter::new,
		EntityAndFilter::filters
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		for (var filter : filters) {
			if (!filter.value().test(entity)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public EntityFilter and(EntityFilter filter) {
		if (filter == ANY.value()) {
			return this;
		} else if (filter == NONE.value()) {
			return filter;
		}

		var list = new ArrayList<>(filters);
		list.add(filter.ref());
		return new EntityAndFilter(List.copyOf(list));
	}
}
