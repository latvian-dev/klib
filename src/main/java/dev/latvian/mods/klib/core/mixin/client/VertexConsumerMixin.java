package dev.latvian.mods.klib.core.mixin.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.latvian.mods.klib.core.api.client.KLibVertexConsumer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin extends KLibVertexConsumer {
}
