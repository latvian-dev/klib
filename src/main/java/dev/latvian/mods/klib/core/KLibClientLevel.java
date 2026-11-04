package dev.latvian.mods.klib.core;

import net.minecraft.client.multiplayer.ClientLevel;

public interface KLibClientLevel extends KLibLevel {
	@Override
	default ClientLevel klib$self() {
		return (ClientLevel) this;
	}
}
