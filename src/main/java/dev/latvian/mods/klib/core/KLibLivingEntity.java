package dev.latvian.mods.klib.core;

import net.minecraft.world.entity.LivingEntity;

public interface KLibLivingEntity extends KLibEntity {
	@Override
	default LivingEntity klib$self() {
		return (LivingEntity) this;
	}
}
