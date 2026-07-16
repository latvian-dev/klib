package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements KLibEntity {
	@Unique
	private boolean klib$isSaving = false;

	@Override
	@Invoker("setLevel")
	public abstract void klib$setLevel(Level level);

	@Override
	public boolean klib$isSaving() {
		return klib$isSaving;
	}

	@Inject(method = "saveWithoutId", at = @At("HEAD"))
	private void klib$beforeSave(ValueOutput output, CallbackInfo ci) {
		klib$isSaving = true;
	}

	@Inject(method = "saveWithoutId", at = @At("RETURN"))
	private void klib$afterSave(ValueOutput output, CallbackInfo ci) {
		klib$isSaving = false;
	}
}
