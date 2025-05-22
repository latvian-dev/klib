package dev.latvian.mods.kmath.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.kmath.VoxelShapeBox;
import dev.latvian.mods.kmath.color.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

public class BoxRenderer {
	public static void quads(PoseStack ms, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, MultiBufferSource buffers, BufferSupplier type, boolean cull, Color color) {
		if (!color.isTransparent()) {
			BoxBuilder.quads(minX, minY, minZ, maxX, maxY, maxZ, ms.last().transform(type.quads(buffers, cull)).withColor(color));
		}
	}

	public static void lines(PoseStack ms, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, MultiBufferSource buffers, BufferSupplier type, Color color) {
		if (!color.isTransparent()) {
			BoxBuilder.lines(minX, minY, minZ, maxX, maxY, maxZ, ms.last().transform(type.lines(buffers)).withColor(color));
		}
	}

	public static void quadsAndLines(PoseStack ms, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, MultiBufferSource buffers, BufferSupplier type, boolean cull, Color color, Color lineColor) {
		quads(ms, minX, minY, minZ, maxX, maxY, maxZ, buffers, type, cull, color);
		lines(ms, minX, minY, minZ, maxX, maxY, maxZ, buffers, type, lineColor);
	}

	public static void voxelShapeBox(PoseStack ms, VoxelShapeBox shape, Vec3 offset, MultiBufferSource buffers, BufferSupplier type, boolean cull, Color color, Color lineColor) {
		if (!shape.edges().isEmpty() && !lineColor.isTransparent()) {
			shape.buildLines(offset, ms.last().transform(type.lines(buffers)).withColor(lineColor));
		}

		if (!shape.boxes().isEmpty() && !color.isTransparent()) {
			shape.buildQuads(offset, ms.last().transform(type.quads(buffers, cull)).withColor(color));
		}
	}

	public static void frame(PoseStack ms, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, MultiBufferSource buffers, BufferSupplier type, boolean cull, Color color, Color lineColor, float cornerSize, float edgeSize) {
		if (!lineColor.isTransparent()) {
			BoxBuilder.frameLines(minX, minY, minZ, maxX, maxY, maxZ, cornerSize, edgeSize, ms.last().transform(type.lines(buffers)).withColor(lineColor));
		}

		if (!color.isTransparent()) {
			BoxBuilder.frameQuads(minX, minY, minZ, maxX, maxY, maxZ, cornerSize, edgeSize, ms.last().transform(type.quads(buffers, cull)).withColor(color));
		}
	}
}