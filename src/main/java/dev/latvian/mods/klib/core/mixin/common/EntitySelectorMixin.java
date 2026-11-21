package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.KLibObject;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public abstract class EntitySelectorMixin implements KLibObject, Predicate<Entity> {
	@Shadow
	@Final
	private List<Predicate<Entity>> contextFreePredicates;

	@Override
	public Object klib$get(int id) {
		return id == 0 ? contextFreePredicates : null;
	}

	@Override
	public boolean test(Entity entity) {
		if (entity == null || !entity.isAlive()) {
			return false;
		}

		for (var predicate : contextFreePredicates) {
			if (!predicate.test(entity)) {
				return false;
			}
		}

		return true;
	}
}
