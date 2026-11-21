package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.KLibObject;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClipContext.class)
public abstract class ClipContextMixin implements KLibObject {
	@Shadow
	@Final
	private ClipContext.Block block;

	@Shadow
	@Final
	private ClipContext.Fluid fluid;

	@Shadow
	@Final
	private CollisionContext collisionContext;

	@Override
	public Object klib$get(int id) {
		return switch (id) {
			case 0 -> block;
			case 1 -> fluid;
			case 2 -> collisionContext;
			default -> null;
		};
	}
}
