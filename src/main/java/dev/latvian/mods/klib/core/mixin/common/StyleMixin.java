package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.common.KLibStyle;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Style.class)
public abstract class StyleMixin implements KLibStyle {
}
