package dev.latvian.mods.kmath.render;

import dev.latvian.mods.kmath.tex.KLight;
import dev.latvian.mods.kmath.tex.KTexture;
import dev.latvian.mods.kmath.util.Rotations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector4f;

public class LaserRenderer {
	public final Vector3d targetPos = new Vector3d(0D, 0D, 0D);
	private float length = 0F;
	private Quaternionf rotationY = RotationAxis.POSITIVE_Y.rotation(0F);
	private Quaternionf rotationX = RotationAxis.POSITIVE_X.rotation(0F);

	public Vector4f color = new Vector4f(0.5F, 0.75F, 1F, 1F);
	public float startRadius = 0.5F;
	public float endRadius = 0.5F;
	public float offset = 0F;
	public KTexture texture = KTexture.WHITE;
	public KLight light = KLight.FULLBRIGHT;

	public void updateTarget() {
		var tx = targetPos.x;
		var ty = targetPos.y;
		var tz = targetPos.z;
		length = (float) Math.sqrt(tx * tx + ty * ty + tz * tz);

		if (length - offset <= 0F) {
			return;
		}

		rotationY = RotationAxis.POSITIVE_Y.rotationDegrees(-Rotations.getYaw(tx / length, tz / length));
		rotationX = RotationAxis.POSITIVE_X.rotationDegrees(90 + Rotations.getPitch(ty / length));
	}

	@Environment(EnvType.CLIENT)
	public void render(MatrixStack matrices, VertexConsumer buffer) {
		float sr = startRadius;
		float er = endRadius;

		if (sr <= 0F && er <= 0F) {
			return;
		}

		if (length - offset <= 0F) {
			return;
		}

		matrices.push();
		matrices.multiply(rotationY);
		matrices.multiply(rotationX);

		var entry = matrices.peek();
		var m = entry.getPositionMatrix();
		var n = entry.getNormalMatrix();
		var uvs = texture.getUVs();
		var u0 = uvs[0].floatValue();
		var v0 = uvs[1].floatValue();
		var u1 = uvs[2].floatValue();
		var v1 = uvs[3].floatValue();

		renderBeamFace(buffer, m, n, sr, er, -1F, -1F, 1F, -1F, 0F, -1F, u0, v0, u1, v1); // north
		renderBeamFace(buffer, m, n, sr, er, -1F, -1F, -1F, 1F, -1F, 0F, u0, v0, u1, v1); // west
		renderBeamFace(buffer, m, n, sr, er, 1F, -1F, 1F, 1F, 1F, 0F, u0, v0, u1, v1); // east
		renderBeamFace(buffer, m, n, sr, er, -1F, 1F, 1F, 1F, 0F, 1F, u0, v0, u1, v1); // south

		matrices.pop();
	}

	@Environment(EnvType.CLIENT)
	private void renderBeamFace(VertexConsumer buffer, Matrix4f m, Matrix3f n, float sr, float er, float x0, float z0, float x1, float z1, float nx, float nz, float u0, float v0, float u1, float v1) {
		float r = color.x;
		float g = color.y;
		float b = color.z;
		float a = color.w;

		int lu = light.lightU;
		int lv = light.lightV;
		int ou = light.overlayU;
		int ov = light.overlayV;

		buffer.vertex(m, x0 * er, length, z0 * er).color(r, g, b, a).texture(u1, v0).overlay(ou, ov).light(lu, lv).normal(n, nx, 0F, nz).next();
		buffer.vertex(m, x0 * sr, offset, z0 * sr).color(r, g, b, a).texture(u1, v1).overlay(ou, ov).light(lu, lv).normal(n, nx, 0F, nz).next();
		buffer.vertex(m, x1 * sr, offset, z1 * sr).color(r, g, b, a).texture(u0, v1).overlay(ou, ov).light(lu, lv).normal(n, nx, 0F, nz).next();
		buffer.vertex(m, x1 * er, length, z1 * er).color(r, g, b, a).texture(u0, v0).overlay(ou, ov).light(lu, lv).normal(n, nx, 0F, nz).next();
	}
}
