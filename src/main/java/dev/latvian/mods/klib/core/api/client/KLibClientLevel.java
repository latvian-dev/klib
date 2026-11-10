package dev.latvian.mods.klib.core.api.client;

import dev.latvian.mods.klib.core.api.common.KLibLevel;
import net.minecraft.client.multiplayer.ClientLevel;

public interface KLibClientLevel extends KLibLevel {
	@Override
	default ClientLevel klib$self() {
		return (ClientLevel) this;
	}
}
