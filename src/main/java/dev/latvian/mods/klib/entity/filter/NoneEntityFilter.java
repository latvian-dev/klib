package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public enum NoneEntityFilter implements EntityFilter {
	INSTANCE;

	@Override
	public UnitType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return EntityFilter.NONE;
	}

	@Override
	public boolean test(Entity entity) {
		return false;
	}

	@Override
	public EntityFilter not() {
		return AnyEntityFilter.INSTANCE;
	}

	@Override
	public String toString() {
		return "none";
	}
}
