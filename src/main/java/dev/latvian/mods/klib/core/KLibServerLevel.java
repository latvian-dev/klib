package dev.latvian.mods.klib.core;

import net.minecraft.server.level.ServerLevel;

public interface KLibServerLevel extends KLibLevel {
	@Override
	default ServerLevel klib$self() {
		return (ServerLevel) this;
	}
}
