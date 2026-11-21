package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.KLibObject;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin<T extends Entity> implements KLibObject {
	@Shadow
	@Final
	private EntityType.EntityFactory<T> factory;

	@Override
	public Object klib$get(int id) {
		return id == 0 ? factory : null;
	}
}
