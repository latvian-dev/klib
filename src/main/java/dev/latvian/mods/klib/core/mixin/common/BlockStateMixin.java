package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.block.KLibBlockStateProperties;
import dev.latvian.mods.klib.core.api.common.KLibBlockState;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockState.class)
public abstract class BlockStateMixin implements KLibBlockState {
	@Unique
	private KLibBlockStateProperties klib$getBlockStateProperties = null;

	@Override
	public KLibBlockStateProperties klib$getBlockStateProperties() {
		if (klib$getBlockStateProperties == null) {
			klib$getBlockStateProperties = KLibBlockStateProperties.MAP.computeIfAbsent((BlockState) (Object) this, KLibBlockStateProperties::compute);
		}

		return klib$getBlockStateProperties;
	}
}
