package dev.latvian.mods.klib.render.vertexconsumer;

import com.mojang.blaze3d.vertex.VertexConsumer;

public record PosVertexConsumer(VertexConsumer delegate) implements DelegateVertexConsumer {
}
