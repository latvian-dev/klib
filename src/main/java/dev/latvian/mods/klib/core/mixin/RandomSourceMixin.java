package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibRandomSource;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RandomSource.class)
public interface RandomSourceMixin extends KLibRandomSource {
}
