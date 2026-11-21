package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.KLibObject;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Level.class)
public abstract class LevelMixin implements KLibObject {
	@Unique
	private Object klib$playerTarget;

	@Override
	public Object klib$get(int id) {
		return id == 492 ? klib$playerTarget : null;
	}

	@Override
	public void klib$set(int id, Object value) {
		if (id == 492) {
			klib$playerTarget = value;
		}
	}
}
