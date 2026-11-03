package dev.latvian.mods.klib.math;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.joml.Vector4f;

public record ProjectedCoordinates(
	Minecraft mc,
	Vec3 cameraPos,
	float width,
	float height,
	Vector2dc defaultScreenPos,
	Matrix4fc worldMatrix,
	Matrix4fc inverseWorldMatrix,
	Vec3 worldPos
) {
	public static ProjectedCoordinates of(Minecraft mc, CameraRenderState camera) {
		return of(mc, camera.pos, camera.projectionMatrix, camera.viewRotationMatrix);
	}

	public static ProjectedCoordinates of(Minecraft mc, Vec3 cameraPos, Matrix4fc projectionMatrix, Matrix4fc viewRotationMatrix) {
		var width = mc.getWindow().getGuiScaledWidth();
		var height = mc.getWindow().getGuiScaledHeight();
		var worldMatrix = new Matrix4f(projectionMatrix);
		worldMatrix.mul(viewRotationMatrix);
		var inverseWorldMatrix = new Matrix4f(worldMatrix).invert();

		var worldPos = new Vector4f(1F / width - 1F, -(1F / height - 1F), 1F, 1F);
		worldPos.mul(inverseWorldMatrix);
		worldPos.div(worldPos.w);

		return new ProjectedCoordinates(
			mc,
			cameraPos,
			width,
			height,
			mc.gui.screen() == null ? new Vector2d(
				width * 0.5D,
				height * 0.5D
			) : new Vector2d(
				mc.mouseHandler.xpos() * width / (double) mc.getWindow().getWidth(),
				mc.mouseHandler.ypos() * height / (double) mc.getWindow().getHeight()
			),
			worldMatrix,
			inverseWorldMatrix,
			cameraPos.add(worldPos.x, worldPos.y, worldPos.z)
		);
	}

	@Nullable
	public ClipPosition clip(double maxDistance, ClipContext.Block blockClipContext, ClipContext.Fluid fluidClipContext, @Nullable Vector2dc screenPos, @Nullable Entity clipEntity) {
		if (screenPos == null) {
			screenPos = defaultScreenPos;
		}

		var worldPos = world(screenPos.x(), screenPos.y());

		var dist = cameraPos.distanceTo(worldPos);
		var lerp = Math.min(1D, maxDistance / dist);

		var hit = mc.level == null || mc.player == null ? null : mc.level.clip(new ClipContext(
			cameraPos,
			cameraPos.lerp(worldPos, lerp),
			blockClipContext,
			fluidClipContext,
			clipEntity == null ? mc.player : clipEntity
		));

		if (hit != null && hit.getType() == HitResult.Type.MISS) {
			hit = null;
		}

		if (hit == null) {
			return null;
		}

		return new ClipPosition(hit);
	}

	@Nullable
	public ClipPosition clipOutline() {
		return clip(1000D, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, null, null);
	}

	@Nullable
	public ClipPosition clipCollision() {
		return clip(1000D, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, null, null);
	}

	/**
	 * Convert world position to screen coordinates
	 *
	 * @param worldX       X position
	 * @param worldY       Y position
	 * @param worldZ       Z position
	 * @param allowOutside Allow outside the screen
	 * @return Screen coordinates, or null if outside the screen
	 */
	public boolean screen(double worldX, double worldY, double worldZ, boolean allowOutside, Vector2f screenPos) {
		double rx = worldX - cameraPos.x;
		double ry = worldY - cameraPos.y;
		double rz = worldZ - cameraPos.z;
		double len = Math.sqrt(rx * rx + ry * ry + rz * rz);

		var v = new Vector4f((float) (rx / len), (float) (ry / len), (float) (rz / len), 1F);
		v.mul(worldMatrix);
		v.div(v.w);

		screenPos.set((0.5F + v.x * 0.5F) * width, (0.5F - v.y * 0.5F) * height);
		return allowOutside || v.z > 0F && v.z < 1F;
	}

	/**
	 * @param worldX X position
	 * @param worldY Y position
	 * @param worldZ Z position
	 * @see ProjectedCoordinates#screen(double, double, double, boolean, Vector2f)
	 */
	public boolean screen(double worldX, double worldY, double worldZ, Vector2f screenPos) {
		return screen(worldX, worldY, worldZ, false, screenPos);
	}

	/**
	 * @param worldPos     XYZ position
	 * @param allowOutside Allow outside the screen
	 * @see ProjectedCoordinates#screen(double, double, double, boolean, Vector2f)
	 */
	public boolean screen(Position worldPos, boolean allowOutside, Vector2f screenPos) {
		return screen(worldPos.x(), worldPos.y(), worldPos.z(), allowOutside, screenPos);
	}

	/**
	 * @param worldPos XYZ position
	 * @see ProjectedCoordinates#screen(double, double, double, boolean, Vector2f)
	 */
	public boolean screen(Position worldPos, Vector2f screenPos) {
		return screen(worldPos.x(), worldPos.y(), worldPos.z(), false, screenPos);
	}

	/**
	 * Convert screen coordinates to world position. Use {@link ProjectedCoordinates#clip(double, ClipContext.Block, ClipContext.Fluid, Vector2dc, Entity)} if you only care about current mouse position
	 *
	 * @param x screen coordinate x-position
	 * @param y screen coordinate y-position
	 * @return a {@link Vec3} containing the screen coordinates in world position
	 */
	public Vec3 world(double x, double y) {
		var v = new Vector4d(x * 2D / width - 1D, -(y * 2D / height - 1F), 1D, 1D);
		v.mul(inverseWorldMatrix);
		v.div(v.w);
		return new Vec3(v.x + cameraPos.x, v.y + cameraPos.y, v.z + cameraPos.z);
	}

	/**
	 * @see ProjectedCoordinates#world(double, double)
	 */
	public void world(double x, double y, Vector3d worldPos) {
		var v = new Vector4d(x * 2D / width - 1D, -(y * 2D / height - 1F), 1D, 1D);
		v.mul(inverseWorldMatrix);
		v.div(v.w);
		worldPos.set(v.x + cameraPos.x, v.y + cameraPos.y, v.z + cameraPos.z);
	}
}
