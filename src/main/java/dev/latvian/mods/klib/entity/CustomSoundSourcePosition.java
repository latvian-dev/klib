package dev.latvian.mods.klib.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface CustomSoundSourcePosition {
	static Vec3 of(Entity entity) {
		return entity instanceof CustomSoundSourcePosition p ? p.getSoundSourcePosition() : entity.getEyePosition();
	}

	Vec3 getSoundSourcePosition();
}
