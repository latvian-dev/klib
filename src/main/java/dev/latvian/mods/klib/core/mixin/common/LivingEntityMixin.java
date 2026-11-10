package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.common.KLibLivingEntity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements KLibLivingEntity {
}
