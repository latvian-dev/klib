package dev.latvian.mods.kmath.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.kmath.VoxelShapeBox;
import dev.latvian.mods.kmath.color.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

public class BoxRenderer {
	// POSITION_COLOR
	public static void renderDebugQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, PoseStack ms, MultiBufferSource buffers, boolean cull, Color color) {
		float colA = color.alphaf();

		if (colA <= 0F) {
			return;
		}

		var e = ms.last();
		var m = e.pose();
		var buffer = buffers.getBuffer(cull ? DebugRenderTypes.QUADS : DebugRenderTypes.QUADS_NO_CULL);

		float colR = color.redf();
		float colG = color.greenf();
		float colB = color.bluef();

		// East
		buffer.addVertex(m, maxX, maxY, minZ).setColor(colR, colG, colB, colA); // N 1 0 0
		buffer.addVertex(m, maxX, maxY, maxZ).setColor(colR, colG, colB, colA); // N 1 0 0
		buffer.addVertex(m, maxX, minY, maxZ).setColor(colR, colG, colB, colA); // N 1 0 0
		buffer.addVertex(m, maxX, minY, minZ).setColor(colR, colG, colB, colA); // N 1 0 0

		// South
		buffer.addVertex(m, maxX, minY, maxZ).setColor(colR, colG, colB, colA); // N 0 0 1
		buffer.addVertex(m, maxX, maxY, maxZ).setColor(colR, colG, colB, colA); // N 0 0 1
		buffer.addVertex(m, minX, maxY, maxZ).setColor(colR, colG, colB, colA); // N 0 0 1
		buffer.addVertex(m, minX, minY, maxZ).setColor(colR, colG, colB, colA); // N 0 0 1

		// North
		buffer.addVertex(m, minX, minY, minZ).setColor(colR, colG, colB, colA); // N 0 0 -1
		buffer.addVertex(m, minX, maxY, minZ).setColor(colR, colG, colB, colA); // N 0 0 -1
		buffer.addVertex(m, maxX, maxY, minZ).setColor(colR, colG, colB, colA); // N 0 0 -1
		buffer.addVertex(m, maxX, minY, minZ).setColor(colR, colG, colB, colA); // N 0 0 -1

		// West
		buffer.addVertex(m, minX, minY, minZ).setColor(colR, colG, colB, colA); // N -1 0 0
		buffer.addVertex(m, minX, minY, maxZ).setColor(colR, colG, colB, colA); // N -1 0 0
		buffer.addVertex(m, minX, maxY, maxZ).setColor(colR, colG, colB, colA); // N -1 0 0
		buffer.addVertex(m, minX, maxY, minZ).setColor(colR, colG, colB, colA); // N -1 0 0

		// Up
		buffer.addVertex(m, minX, maxY, minZ).setColor(colR, colG, colB, colA); // N 0 1 0
		buffer.addVertex(m, minX, maxY, maxZ).setColor(colR, colG, colB, colA); // N 0 1 0
		buffer.addVertex(m, maxX, maxY, maxZ).setColor(colR, colG, colB, colA); // N 0 1 0
		buffer.addVertex(m, maxX, maxY, minZ).setColor(colR, colG, colB, colA); // N 0 1 0

		// Down
		buffer.addVertex(m, minX, minY, minZ).setColor(colR, colG, colB, colA); // N 0 -1 0
		buffer.addVertex(m, maxX, minY, minZ).setColor(colR, colG, colB, colA); // N 0 -1 0
		buffer.addVertex(m, maxX, minY, maxZ).setColor(colR, colG, colB, colA); // N 0 -1 0
		buffer.addVertex(m, minX, minY, maxZ).setColor(colR, colG, colB, colA); // N 0 -1 0

	}

	// POSITION_COLOR
	public static void renderDebugLines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, PoseStack ms, MultiBufferSource buffers, Color color) {
		float colA = color.alphaf();

		if (colA <= 0F) {
			return;
		}

		var e = ms.last();
		var m = e.pose();
		var buffer = buffers.getBuffer(DebugRenderTypes.LINES);

		float colR = color.redf();
		float colG = color.greenf();
		float colB = color.bluef();

		buffer.addVertex(m, minX, minY, minZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, maxX, minY, minZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, minX, minY, minZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, minX, maxY, minZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, minX, minY, minZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, minX, minY, maxZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, maxX, minY, minZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, maxX, maxY, minZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, maxX, maxY, minZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, minX, maxY, minZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, minX, maxY, minZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, minX, maxY, maxZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, minX, maxY, maxZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, minX, minY, maxZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, minX, minY, maxZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, maxX, minY, maxZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, maxX, minY, maxZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, maxX, minY, minZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, minX, maxY, maxZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, maxX, maxY, maxZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, maxX, minY, maxZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, maxX, maxY, maxZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, maxX, maxY, minZ).setColor(colR, colG, colB, colA);
		buffer.addVertex(m, maxX, maxY, maxZ).setColor(colR, colG, colB, colA);
	}

	public static void renderDebugQuadsAndLines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, PoseStack ms, MultiBufferSource buffers, boolean cull, Color color, Color lineColor) {
		renderDebugQuads(minX, minY, minZ, maxX, maxY, maxZ, ms, buffers, cull, color);
		renderDebugLines(minX, minY, minZ, maxX, maxY, maxZ, ms, buffers, lineColor);
	}

	// POSITION_COLOR
	public static void renderVoxelShape(PoseStack ms, MultiBufferSource buffers, VoxelShapeBox shape, Vec3 offset, boolean cull, Color color, Color lineColor) {
		if (shape.boxes().isEmpty() && shape.edges().isEmpty()) {
			return;
		}

		var e = ms.last();
		var m = e.pose();

		float lcolA = lineColor.alphaf();

		if (lcolA > 0F) {
			var buffer = buffers.getBuffer(DebugRenderTypes.LINES);

			float lcolR = lineColor.redf();
			float lcolG = lineColor.greenf();
			float lcolB = lineColor.bluef();

			for (var edge : shape.edges()) {
				float minX = (float) (edge.start().x + offset.x);
				float minY = (float) (edge.start().y + offset.y);
				float minZ = (float) (edge.start().z + offset.z);
				float maxX = (float) (edge.end().x + offset.x);
				float maxY = (float) (edge.end().y + offset.y);
				float maxZ = (float) (edge.end().z + offset.z);

				buffer.addVertex(m, minX, minY, minZ).setColor(lcolR, lcolG, lcolB, lcolA);
				buffer.addVertex(m, maxX, maxY, maxZ).setColor(lcolR, lcolG, lcolB, lcolA);
			}
		}

		if (!color.isTransparent()) {
			for (var box : shape.boxes()) {
				float minX = (float) (box.minX + offset.x);
				float minY = (float) (box.minY + offset.y);
				float minZ = (float) (box.minZ + offset.z);
				float maxX = (float) (box.maxX + offset.x);
				float maxY = (float) (box.maxY + offset.y);
				float maxZ = (float) (box.maxZ + offset.z);
				renderDebugQuads(minX, minY, minZ, maxX, maxY, maxZ, ms, buffers, cull, color);
			}
		}
	}

	public static void renderDebugFrame(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, PoseStack ms, MultiBufferSource buffers, boolean cull, Color color, Color lineColor, float cornerSize, float edgeSize) {
		float c = cornerSize * 0.5F;
		float e = edgeSize * 0.5F;
		float o = c * 2F;

		renderDebugQuadsAndLines(minX - c, minY - c, minZ - c, minX + c, minY + c, minZ + c, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(minX - c, minY - c, maxZ - c, minX + c, minY + c, maxZ + c, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(minX - c, maxY - c, minZ - c, minX + c, maxY + c, minZ + c, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(minX - c, maxY - c, maxZ - c, minX + c, maxY + c, maxZ + c, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(maxX - c, minY - c, minZ - c, maxX + c, minY + c, minZ + c, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(maxX - c, minY - c, maxZ - c, maxX + c, minY + c, maxZ + c, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(maxX - c, maxY - c, minZ - c, maxX + c, maxY + c, minZ + c, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(maxX - c, maxY - c, maxZ - c, maxX + c, maxY + c, maxZ + c, ms, buffers, cull, color, lineColor);

		renderDebugQuadsAndLines(minX - e, minY - e, minZ - c + o, minX + e, minY + e, maxZ + c - o, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(maxX - e, minY - e, minZ - c + o, maxX + e, minY + e, maxZ + c - o, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(minX - c + o, minY - e, minZ - e, maxX + c - o, minY + e, minZ + e, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(minX - c + o, minY - e, maxZ - e, maxX + c - o, minY + e, maxZ + e, ms, buffers, cull, color, lineColor);

		renderDebugQuadsAndLines(minX - e, maxY - e, minZ - c + o, minX + e, maxY + e, maxZ + c - o, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(maxX - e, maxY - e, minZ - c + o, maxX + e, maxY + e, maxZ + c - o, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(minX - c + o, maxY - e, minZ - e, maxX + c - o, maxY + e, minZ + e, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(minX - c + o, maxY - e, maxZ - e, maxX + c - o, maxY + e, maxZ + e, ms, buffers, cull, color, lineColor);

		renderDebugQuadsAndLines(minX - e, minY - c + o, minZ - e, minX + e, maxY + c - o, minZ + e, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(maxX - e, minY - c + o, minZ - e, maxX + e, maxY + c - o, minZ + e, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(minX - e, minY - c + o, maxZ - e, minX + e, maxY + c - o, maxZ + e, ms, buffers, cull, color, lineColor);
		renderDebugQuadsAndLines(maxX - e, minY - c + o, maxZ - e, maxX + e, maxY + c - o, maxZ + e, ms, buffers, cull, color, lineColor);
	}
}