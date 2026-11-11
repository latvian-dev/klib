package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.common.KLibUseOnContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(UseOnContext.class)
public abstract class UseOnContextMixin implements KLibUseOnContext {
	@Override
	@Invoker("getHitResult")
	public abstract BlockHitResult klib$getHitResult();
}
