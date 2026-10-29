package dev.latvian.mods.klib.icon;

import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.network.RegistryFriendlyByteBuf;

public enum EmptyIcon implements Icon {
	INSTANCE;

	@Override
	public UnitType<RegistryFriendlyByteBuf, Icon> type() {
		return EMPTY;
	}

	@Override
	public String toString() {
		return "empty";
	}
}
