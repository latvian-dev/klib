package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibItemEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements KLibItemEntity {
}
