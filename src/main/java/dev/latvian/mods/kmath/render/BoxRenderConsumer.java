package dev.latvian.mods.kmath.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.shape.VoxelShapes;

public record BoxRenderConsumer(MatrixStack matrices, VertexConsumer lines, float red, float green, float blue, float alpha) implements VoxelShapes.BoxConsumer {
	@Override
	public void consume(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		LineBoxRenderer.lineBox(matrices, lines, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
	}
}
