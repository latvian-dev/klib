package dev.latvian.mods.kmath.util;

import dev.latvian.mods.kmath.KMath;
import dev.latvian.mods.kmath.render.LaserRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.random.Random;
import org.joml.Math;
import org.joml.Matrix3d;
import org.joml.Vector3d;

public class LightningPoints implements DeltaTicking {
	public final LaserRenderer laser;
	public final int segments;
	public final Random random;
	public final float[] prevAngles;
	public final float[] prevDist;
	public final float[] angles;
	public final float[] dist;
	public float spread;
	public final Vector3d target = new Vector3d();
	private final Matrix3d matrix = new Matrix3d();

	public LightningPoints(LaserRenderer laser, int segments, Random random) {
		this.laser = laser;
		this.segments = segments;
		this.random = random;
		this.prevAngles = new float[segments - 1];
		this.prevDist = new float[segments - 1];
		this.angles = new float[segments - 1];
		this.dist = new float[segments - 1];
		this.spread = 2F;
	}

	@Environment(EnvType.CLIENT)
	public void render(MatrixStack ms, float delta, VertexConsumer buffer) {
		var length = target.length();

		if (length <= 0F) {
			return;
		}

		var startRadius = laser.startRadius;
		var endRadius = laser.endRadius;

		var prevPoint = new Vector3d(0D, 0D, 0D);
		var point = new Vector3d(0D, 0D, 0D);

		matrix.identity();
		matrix.rotateY(Math.toRadians(-Rotations.getYaw(target.x / length, target.z / length)));
		matrix.rotateX(Math.toRadians(90 + Rotations.getPitch(target.y / length)));

		for (int i = 0; i < angles.length; i++) {
			laser.startRadius = i == 0 ? endRadius : startRadius;
			laser.endRadius = startRadius;

			var angle = KMath.lerp(delta, prevAngles[i], angles[i]) * Math.PI * 2D;
			var d = KMath.lerp(delta, prevDist[i], dist[i]) * spread;

			prevPoint.set(point);

			point.set(Math.cos(angle) * d, (i + 1F) * length / (segments + 1F), Math.sin(angle) * d).mul(matrix);

			ms.push();
			ms.translate(prevPoint.x, prevPoint.y, prevPoint.z);
			laser.targetPos.set(point.x - prevPoint.x, point.y - prevPoint.y, point.z - prevPoint.z);
			laser.updateTarget();
			laser.render(ms, buffer);
			ms.pop();
		}

		laser.startRadius = startRadius;
		laser.endRadius = endRadius;

		prevPoint.set(point);
		point.set(0D, length, 0D).mul(matrix);

		ms.push();
		ms.translate(prevPoint.x, prevPoint.y, prevPoint.z);
		laser.targetPos.set(point.x - prevPoint.x, point.y - prevPoint.y, point.z - prevPoint.z);
		laser.updateTarget();
		laser.render(ms, buffer);
		ms.pop();
	}

	@Override
	public void snap() {
		for (int i = 0; i < angles.length; i++) {
			prevAngles[i] = angles[i];
			prevDist[i] = dist[i];
		}
	}

	@Override
	public void tickValue() {
		for (int i = 0; i < angles.length; i++) {
			angles[i] = random.nextFloat();
			dist[i] = random.nextFloat() * spread;
		}
	}
}
