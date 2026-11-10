package dev.latvian.mods.klib.core.api.common;

import net.minecraft.world.level.Level;

public interface KLibLevel {
	default Level klib$self() {
		return (Level) this;
	}
}
