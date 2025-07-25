package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibStreamCodec;
import net.minecraft.network.codec.StreamCodec;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StreamCodec.class)
public interface StreamCodecMixin<B, V> extends KLibStreamCodec<B, V> {
}
