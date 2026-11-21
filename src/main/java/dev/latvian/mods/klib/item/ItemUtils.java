package dev.latvian.mods.klib.item;

import dev.latvian.mods.klib.core.KLibExtensions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;

public interface ItemUtils {
	static BlockHitResult getHitResult(UseOnContext ctx) {
		return KLibExtensions.get(ctx, 0);
	}

	static void partiallyMergeCustomData(ItemStack stack, CompoundTag tag) {
		var data = stack.get(DataComponents.CUSTOM_DATA);

		if (data == null || data.isEmpty()) {
			stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		} else {
			var newTag = data.copyTag();

			for (var key : tag.keySet()) {
				newTag.put(key, tag.get(key));
			}

			stack.set(DataComponents.CUSTOM_DATA, CustomData.of(newTag));
		}
	}
}
