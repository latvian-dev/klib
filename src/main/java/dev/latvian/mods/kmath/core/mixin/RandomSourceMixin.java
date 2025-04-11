package dev.latvian.mods.kmath.core.mixin;

import dev.latvian.mods.kmath.core.KMathRandomSource;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RandomSource.class)
public interface RandomSourceMixin extends KMathRandomSource {
}
