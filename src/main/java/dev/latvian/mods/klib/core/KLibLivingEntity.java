package dev.latvian.mods.klib.core;

import net.minecraft.world.entity.LivingEntity;

public interface KLibLivingEntity extends KLibEntity {
	@Override
	default LivingEntity klib$self() {
		return (LivingEntity) this;
	}

	@Override
	default float klib$getHealth(float delta) {
		return klib$self().getHealth();
	}

	@Override
	default float klib$getMaxHealth(float delta) {
		return klib$self().getMaxHealth();
	}
}
