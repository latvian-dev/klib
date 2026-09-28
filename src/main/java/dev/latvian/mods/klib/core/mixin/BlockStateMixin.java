package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibBlockState;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockState.class)
public abstract class BlockStateMixin implements KLibBlockState {
	@Unique
	private Boolean klib$visible = null;

	@Unique
	private Boolean klib$partial = null;

	@Unique
	private Float klib$density = null;

	@Override
	public boolean klib$isVisible() {
		if (klib$visible == null) {
			klib$visible = KLibBlockState.super.klib$isVisible();
		}

		return klib$visible;
	}

	@Override
	public boolean klib$isPartial() {
		if (klib$partial == null) {
			klib$partial = KLibBlockState.super.klib$isPartial();
		}

		return klib$partial;
	}

	@Override
	public float klib$getDensity() {
		if (klib$density == null) {
			klib$density = KLibBlockState.super.klib$getDensity();
		}

		return klib$density;
	}
}
