package dev.latvian.mods.kmath.core.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.latvian.mods.kmath.core.KMathVertexConsumer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin extends KMathVertexConsumer {
}
