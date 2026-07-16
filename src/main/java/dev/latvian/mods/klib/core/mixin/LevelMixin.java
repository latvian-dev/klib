package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public abstract class LevelMixin implements KLibLevel {
}
