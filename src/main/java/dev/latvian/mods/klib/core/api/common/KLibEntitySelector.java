package dev.latvian.mods.klib.core.api.common;

import dev.latvian.mods.klib.core.NoMixinException;
import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;

public interface KLibEntitySelector extends Predicate<Entity> {
	@Override
	default boolean test(Entity entity) {
		throw new NoMixinException(this);
	}
}
