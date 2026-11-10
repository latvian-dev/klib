package dev.latvian.mods.klib.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface CustomSoundSourcePosition {
	static Vec3 of(Entity entity, float delta) {
		return entity instanceof CustomSoundSourcePosition p ? p.getSoundSourcePosition(delta) : EntityUtils.getPosition(entity, delta, entity.getEyeHeight());
	}

	Vec3 getSoundSourcePosition(float delta);
}
