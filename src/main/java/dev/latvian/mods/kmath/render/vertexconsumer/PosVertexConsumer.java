package dev.latvian.mods.kmath.render.vertexconsumer;

import com.mojang.blaze3d.vertex.VertexConsumer;

public record PosVertexConsumer(VertexConsumer delegate) implements DelegateVertexConsumer {
}
