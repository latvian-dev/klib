package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibStyle;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Style.class)
public abstract class StyleMixin implements KLibStyle {
}
