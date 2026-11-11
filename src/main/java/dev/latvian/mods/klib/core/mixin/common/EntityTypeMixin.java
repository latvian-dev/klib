package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.common.KLibEntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin<T extends Entity> implements KLibEntityType<T> {
	@Override
	@Accessor("factory")
	public abstract EntityType.EntityFactory<T> klib$getFactory();
}
