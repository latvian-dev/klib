package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibStreamCodec;
import net.minecraft.network.codec.StreamCodec;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StreamCodec.class)
public class StreamCodecMixin<B, V> implements KLibStreamCodec<B, V> {
}
