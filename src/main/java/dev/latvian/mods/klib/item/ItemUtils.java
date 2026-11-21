package dev.latvian.mods.klib.item;

import dev.latvian.mods.klib.core.KLibExtensions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;

public interface ItemUtils {
	static BlockHitResult getHitResult(UseOnContext ctx) {
		return KLibExtensions.get(ctx, 0);
	}
}
