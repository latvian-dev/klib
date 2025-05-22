package dev.latvian.mods.kmath.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.kmath.SpherePoints;
import dev.latvian.mods.kmath.color.Color;
import dev.latvian.mods.kmath.texture.LightUV;
import dev.latvian.mods.kmath.texture.OverlayUV;
import dev.latvian.mods.kmath.texture.UV;
import dev.latvian.mods.kmath.vertex.VertexCallback;
import net.minecraft.client.renderer.MultiBufferSource;

public class SphereRenderer {
	public static void entity(SpherePoints points, PoseStack ms, Color color, UV uv, LightUV light, OverlayUV overlay, VertexCallback callback) {
		points.buildQuads(uv, ms.last().transform(callback).withColor(color).withLight(light).withOverlay(overlay));
	}

	public static void entity(SpherePoints points, PoseStack ms, VertexCallback callback) {
		entity(points, ms, Color.WHITE, UV.FULL, LightUV.NONE, OverlayUV.NORMAL, callback);
	}

	public static void debugQuads(SpherePoints points, PoseStack ms, MultiBufferSource buffers, boolean cull, Color color) {
		debugQuads(points, ms, color, buffers.getBuffer(cull ? DebugRenderTypes.QUADS : DebugRenderTypes.QUADS_NO_CULL));
	}

	public static void debugQuads(SpherePoints points, PoseStack ms, Color color, VertexCallback callback) {
		points.buildQuads(UV.FULL, ms.last().transform(callback).onlyPos().withColor(color));
	}

	public static void debugLines(SpherePoints points, PoseStack ms, Color color, VertexCallback callback) {
		points.buildLines(ms.last().transform(callback).withColor(color));
	}
}