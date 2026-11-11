package dev.latvian.mods.klib.core.api.common;

import dev.latvian.mods.klib.core.NoMixinException;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface KLibEntityType<T extends Entity> {
	default EntityType.EntityFactory<T> klib$getFactory() {
		throw new NoMixinException(this);
	}
}
