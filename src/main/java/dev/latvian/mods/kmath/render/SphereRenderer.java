package dev.latvian.mods.kmath.render;

import dev.latvian.mods.kmath.KMath;
import dev.latvian.mods.kmath.tex.KLight;
import dev.latvian.mods.kmath.tex.KTexture;
import dev.latvian.mods.kmath.util.SpherePoints;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector4f;

public class SphereRenderer {
	public final SpherePoints points;
	public Vector4f color = new Vector4f(1F, 1F, 1F, 1F);
	public KTexture texture = KTexture.WHITE;
	public KLight light = KLight.NORMAL;

	public SphereRenderer(SpherePoints points) {
		this.points = points;
	}

	// POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
	@Environment(EnvType.CLIENT)
	public void renderEntity(MatrixStack ms, VertexConsumer layer) {
		var e = ms.peek();
		var m = e.getPositionMatrix();
		var n = e.getNormalMatrix();

		var uvs = texture.getUVs();
		var u0 = uvs[0].floatValue();
		var v0 = uvs[1].floatValue();
		var u1 = uvs[2].floatValue();
		var v1 = uvs[3].floatValue();

		int ou = light.overlayU;
		int ov = light.overlayV;
		int lu = light.lightU;
		int lv = light.lightV;

		float colR = color.x;
		float colG = color.y;
		float colB = color.z;
		float colA = color.w;

		for (int r = 0; r < points.rows.length - 1; r++) {
			for (int c = 0; c < points.cols.length - 1; c++) {
				var cr = points.rows[r];
				var nr = points.rows[r + 1];
				var cc = points.cols[c];
				var nc = points.cols[c + 1];
				var nv = points.normals[c][r];

				var u0l = KMath.lerp(cc.u(), u0, u1);
				var v0l = KMath.lerp(cr.v(), v0, v1);
				var u1l = KMath.lerp(nc.u(), u0, u1);
				var v1l = KMath.lerp(nr.v(), v0, v1);

				layer.vertex(m, cc.x() * nr.m(), nr.y(), cc.z() * nr.m()).color(colR, colG, colB, colA).texture(u0l, v1l).overlay(ou, ov).light(lu, lv).normal(n, nv.x, nv.y, nv.z).next();
				layer.vertex(m, cc.x() * cr.m(), cr.y(), cc.z() * cr.m()).color(colR, colG, colB, colA).texture(u0l, v0l).overlay(ou, ov).light(lu, lv).normal(n, nv.x, nv.y, nv.z).next();
				layer.vertex(m, nc.x() * cr.m(), cr.y(), nc.z() * cr.m()).color(colR, colG, colB, colA).texture(u1l, v0l).overlay(ou, ov).light(lu, lv).normal(n, nv.x, nv.y, nv.z).next();
				layer.vertex(m, nc.x() * nr.m(), nr.y(), nc.z() * nr.m()).color(colR, colG, colB, colA).texture(u1l, v1l).overlay(ou, ov).light(lu, lv).normal(n, nv.x, nv.y, nv.z).next();
			}
		}
	}
}
