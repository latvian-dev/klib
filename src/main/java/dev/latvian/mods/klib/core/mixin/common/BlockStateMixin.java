package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.KLibObject;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockState.class)
public abstract class BlockStateMixin implements KLibObject {
	@Unique
	private Object klib$properties;

	@Override
	public Object klib$get(int id) {
		return id == 0 ? klib$properties : null;
	}

	@Override
	public void klib$set(int id, Object value) {
		if (id == 0) {
			klib$properties = value;
		}
	}
}
