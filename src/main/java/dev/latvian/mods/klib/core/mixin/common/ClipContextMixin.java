package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.common.KLibClipContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClipContext.class)
public abstract class ClipContextMixin implements KLibClipContext {
	@Override
	@Accessor("block")
	public abstract ClipContext.Block klib$getBlock();

	@Override
	@Accessor("fluid")
	public abstract ClipContext.Fluid klib$getFluid();

	@Override
	@Accessor("collisionContext")
	public abstract CollisionContext klib$getCollisionContext();
}
