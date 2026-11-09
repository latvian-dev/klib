package dev.latvian.mods.klib.core;

import dev.latvian.mods.klib.entity.ServerPlayerContainer;
import net.minecraft.server.level.ServerLevel;

public interface KLibServerLevel extends KLibLevel, ServerPlayerContainer {
	@Override
	default ServerLevel klib$self() {
		return (ServerLevel) this;
	}
}
