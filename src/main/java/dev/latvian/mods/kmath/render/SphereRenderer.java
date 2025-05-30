package dev.latvian.mods.kmath.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.kmath.SpherePoints;
import dev.latvian.mods.kmath.color.Color;
import dev.latvian.mods.kmath.texture.LightUV;
import dev.latvian.mods.kmath.texture.OverlayUV;
import dev.latvian.mods.kmath.texture.UV;
import dev.latvian.mods.kmath.vertex.VertexCallback;
import net.minecraft.client.renderer.MultiBufferSource;

public interface SphereRenderer {
	static void entity(PoseStack ms, float x, float y, float z, float s, SpherePoints points, Color color, UV uv, LightUV light, OverlayUV overlay, VertexCallback callback) {
		points.buildQuads(x, y, z, s, uv, ms.last().transform(callback).withColor(color).withLight(light).withOverlay(overlay));
	}

	static void entity(PoseStack ms, float x, float y, float z, float s, SpherePoints points, VertexCallback callback) {
		entity(ms, x, y, z, s, points, Color.WHITE, UV.FULL, LightUV.NONE, OverlayUV.NORMAL, callback);
	}

	static void quads(PoseStack ms, float x, float y, float z, float s, SpherePoints points, MultiBufferSource buffers, BufferSupplier type, boolean cull, Color color) {
		quads(ms, x, y, z, s, points, color, type.quads(buffers, cull));
	}

	static void quads(PoseStack ms, float x, float y, float z, float s, SpherePoints points, Color color, VertexCallback callback) {
		points.buildQuads(x, y, z, s, UV.FULL, ms.last().transform(callback).withColor(color));
	}

	static void lines(PoseStack ms, float x, float y, float z, float s, SpherePoints points, Color color, VertexCallback callback) {
		points.buildLines(x, y, z, s, ms.last().transform(callback).withColor(color));
	}

	static void lines(PoseStack ms, float x, float y, float z, float s, SpherePoints points, MultiBufferSource buffers, BufferSupplier type, Color color) {
		points.buildLines(x, y, z, s, ms.last().transform(type.lines(buffers)).withColor(color));
	}
}