package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.KLibObject;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements KLibObject {
	@Unique
	private boolean klib$isSaving = false;

	@Shadow
	protected abstract void setLevel(Level level);

	@Inject(method = "saveWithoutId", at = @At("HEAD"))
	private void klib$beforeSave(ValueOutput output, CallbackInfo ci) {
		klib$isSaving = true;
	}

	@Inject(method = "saveWithoutId", at = @At("RETURN"))
	private void klib$afterSave(ValueOutput output, CallbackInfo ci) {
		klib$isSaving = false;
	}

	@Override
	public Object klib$get(int id) {
		return id == 0 ? klib$isSaving : null;
	}

	@Override
	public void klib$set(int id, Object value) {
		if (id == 0) {
			setLevel((Level) value);
		}
	}
}
