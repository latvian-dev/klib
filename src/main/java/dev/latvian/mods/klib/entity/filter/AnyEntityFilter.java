package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public enum AnyEntityFilter implements EntityFilter {
	INSTANCE;

	@Override
	public UnitType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return EntityFilter.ANY;
	}

	@Override
	public boolean test(Entity entity) {
		return true;
	}

	@Override
	public EntityFilter not() {
		return NoneEntityFilter.INSTANCE;
	}

	@Override
	public String toString() {
		return "any";
	}
}
