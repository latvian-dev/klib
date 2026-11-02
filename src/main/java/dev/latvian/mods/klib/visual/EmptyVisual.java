package dev.latvian.mods.klib.visual;

import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.network.RegistryFriendlyByteBuf;

public enum EmptyVisual implements Visual {
	INSTANCE;

	@Override
	public UnitType<RegistryFriendlyByteBuf, Visual> type() {
		return EMPTY;
	}

	@Override
	public String toString() {
		return "empty";
	}
}
