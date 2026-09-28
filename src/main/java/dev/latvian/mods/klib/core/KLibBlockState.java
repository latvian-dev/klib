package dev.latvian.mods.klib.core;

import dev.latvian.mods.klib.util.BlockUtils;
import net.minecraft.world.level.block.state.BlockState;

public interface KLibBlockState {
	default boolean klib$isVisible() {
		return BlockUtils.computeIsVisible((BlockState) this);
	}

	default boolean klib$isPartial() {
		return BlockUtils.computeIsPartial((BlockState) this);
	}

	default float klib$getDensity() {
		return BlockUtils.DENSITY.computeIfAbsent((BlockState) this, BlockUtils::computeDensity);
	}
}
