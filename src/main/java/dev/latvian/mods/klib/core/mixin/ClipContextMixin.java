package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibClipContext;
import net.minecraft.world.level.ClipContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClipContext.class)
public abstract class ClipContextMixin implements KLibClipContext {
}
