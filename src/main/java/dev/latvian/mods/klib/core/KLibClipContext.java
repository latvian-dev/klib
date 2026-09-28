package dev.latvian.mods.klib.core;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.jetbrains.annotations.Nullable;

public interface KLibClipContext {
	@Nullable
	default Entity klib$getEntity() {
		return ((ClipContext) this).collisionContext instanceof EntityCollisionContext ctx ? ctx.getEntity() : null;
	}
}
