package dev.latvian.mods.klib.core.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.latvian.mods.klib.core.KLibVertexConsumer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin extends KLibVertexConsumer {
}
