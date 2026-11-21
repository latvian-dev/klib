package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.KLibObject;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(UseOnContext.class)
public abstract class UseOnContextMixin implements KLibObject {
	@Shadow
	protected abstract BlockHitResult getHitResult();

	@Override
	public Object klib$get(int id) {
		return id == 0 ? getHitResult() : null;
	}
}
