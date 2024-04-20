package dev.latvian.mods.kmath.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class LineBoxRenderer {
	@Environment(EnvType.CLIENT)
	public static void lineBox(MatrixStack matrices, VertexConsumer lines, float x1, float y1, float z1, float x2, float y2, float z2, float red, float green, float blue, float alpha) {
		var m = matrices.peek().getPositionMatrix();
		var n = matrices.peek().getNormalMatrix();
		lines.vertex(m, x1, y1, z1).color(red, green, blue, alpha).normal(n, 1F, 0F, 0F).next();
		lines.vertex(m, x2, y1, z1).color(red, green, blue, alpha).normal(n, 1F, 0F, 0F).next();
		lines.vertex(m, x1, y1, z1).color(red, green, blue, alpha).normal(n, 0F, 1F, 0F).next();
		lines.vertex(m, x1, y2, z1).color(red, green, blue, alpha).normal(n, 0F, 1F, 0F).next();
		lines.vertex(m, x1, y1, z1).color(red, green, blue, alpha).normal(n, 0F, 0F, 1F).next();
		lines.vertex(m, x1, y1, z2).color(red, green, blue, alpha).normal(n, 0F, 0F, 1F).next();
		lines.vertex(m, x2, y1, z1).color(red, green, blue, alpha).normal(n, 0F, 1F, 0F).next();
		lines.vertex(m, x2, y2, z1).color(red, green, blue, alpha).normal(n, 0F, 1F, 0F).next();
		lines.vertex(m, x2, y2, z1).color(red, green, blue, alpha).normal(n, -1F, 0F, 0F).next();
		lines.vertex(m, x1, y2, z1).color(red, green, blue, alpha).normal(n, -1F, 0F, 0F).next();
		lines.vertex(m, x1, y2, z1).color(red, green, blue, alpha).normal(n, 0F, 0F, 1F).next();
		lines.vertex(m, x1, y2, z2).color(red, green, blue, alpha).normal(n, 0F, 0F, 1F).next();
		lines.vertex(m, x1, y2, z2).color(red, green, blue, alpha).normal(n, 0F, -1F, 0F).next();
		lines.vertex(m, x1, y1, z2).color(red, green, blue, alpha).normal(n, 0F, -1F, 0F).next();
		lines.vertex(m, x1, y1, z2).color(red, green, blue, alpha).normal(n, 1F, 0F, 0F).next();
		lines.vertex(m, x2, y1, z2).color(red, green, blue, alpha).normal(n, 1F, 0F, 0F).next();
		lines.vertex(m, x2, y1, z2).color(red, green, blue, alpha).normal(n, 0F, 0F, -1F).next();
		lines.vertex(m, x2, y1, z1).color(red, green, blue, alpha).normal(n, 0F, 0F, -1F).next();
		lines.vertex(m, x1, y2, z2).color(red, green, blue, alpha).normal(n, 1F, 0F, 0F).next();
		lines.vertex(m, x2, y2, z2).color(red, green, blue, alpha).normal(n, 1F, 0F, 0F).next();
		lines.vertex(m, x2, y1, z2).color(red, green, blue, alpha).normal(n, 0F, 1F, 0F).next();
		lines.vertex(m, x2, y2, z2).color(red, green, blue, alpha).normal(n, 0F, 1F, 0F).next();
		lines.vertex(m, x2, y2, z1).color(red, green, blue, alpha).normal(n, 0F, 0F, 1F).next();
		lines.vertex(m, x2, y2, z2).color(red, green, blue, alpha).normal(n, 0F, 0F, 1F).next();
	}

	@Environment(EnvType.CLIENT)
	public static void lineBox(MatrixStack matrices, VertexConsumer lines, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
		lineBox(matrices, lines, (float) x1, (float) y1, (float) z1, (float) x2, (float) y2, (float) z2, red, green, blue, alpha);
	}
}
