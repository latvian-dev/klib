package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public record EntityOrFilter(List<Ref<EntityFilter>> filters) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"or",
		"filters",
		EntityFilter.CODEC.listOf(),
		KLibStreamCodecs.listOf(EntityFilter.STREAM_CODEC),
		EntityOrFilter::new,
		EntityOrFilter::filters
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		for (var filter : filters) {
			if (filter.value().test(entity)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public EntityFilter or(EntityFilter filter) {
		if (filter == ANY.value()) {
			return filter;
		} else if (filter == NONE.value()) {
			return this;
		}

		var list = new ArrayList<>(filters);
		list.add(filter.ref());
		return new EntityOrFilter(List.copyOf(list));
	}
}
