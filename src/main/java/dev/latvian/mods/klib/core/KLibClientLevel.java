package dev.latvian.mods.klib.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public interface KLibClientLevel extends KLibLevel {
	@Override
	default float klib$getDelta() {
		return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
	}

	@Override
	default Iterable<Entity> klib$allEntities() {
		return ((ClientLevel) this).entitiesForRendering();
	}
}
