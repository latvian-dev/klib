package dev.latvian.mods.klib.math;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

public record WorldMouse(
	Minecraft mc,
	Matrix4fc worldMatrix,
	Matrix4fc invertedWorldMatrix,
	Vec3 cameraPos,
	float width,
	float height,
	Vec2f defaultScreenPos
) {
	public static WorldMouse of(Minecraft mc, Vec3 cameraPos, Matrix4fc worldMatrix) {
		var width = mc.getWindow().getGuiScaledWidth();
		var height = mc.getWindow().getGuiScaledHeight();

		return new WorldMouse(
			mc,
			new Matrix4f(worldMatrix),
			new Matrix4f(worldMatrix).invert(),
			cameraPos,
			width,
			height,
			mc.screen == null ? new Vec2f(
				width * 0.5F,
				height * 0.5F
			) : new Vec2f(
				(float) (mc.mouseHandler.xpos() * width / (double) mc.getWindow().getWidth()),
				(float) (mc.mouseHandler.ypos() * height / (double) mc.getWindow().getHeight())
			)
		);
	}

	@Nullable
	public Cursor clip(double maxDistance, ClipContext.Block blockClipContext, ClipContext.Fluid fluidClipContext, @Nullable Vec2f screenPos, @Nullable Entity clipEntity) {
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

		return new Cursor(hit);
	}

	@Nullable
	public Cursor clipOutline() {
		return clip(1000D, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, null, null);
	}

	@Nullable
	public Cursor clipCollision() {
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
	@Nullable
	public Vec2f screen(double worldX, double worldY, double worldZ, boolean allowOutside) {
		var v = new Vector4f((float) (worldX - cameraPos.x), (float) (worldY - cameraPos.y), (float) (worldZ - cameraPos.z), 1F);
		v.mul(worldMatrix);
		v.div(v.w);

		if (allowOutside || v.z > 0F && v.z < 1F) {
			return new Vec2f(
				(0.5F + v.x * 0.5F) * width,
				(0.5F - v.y * 0.5F) * height
			);
		}

		return null;
	}

	/**
	 * @param worldX X position
	 * @param worldY Y position
	 * @param worldZ Z position
	 * @see WorldMouse#screen(double, double, double, boolean)
	 */
	@Nullable
	public Vec2f screen(double worldX, double worldY, double worldZ) {
		return screen(worldX, worldY, worldZ, false);
	}

	/**
	 * @param worldPos     XYZ position
	 * @param allowOutside Allow outside the screen
	 * @see WorldMouse#screen(double, double, double, boolean)
	 */
	@Nullable
	public Vec2f screen(Position worldPos, boolean allowOutside) {
		return screen(worldPos.x(), worldPos.y(), worldPos.z(), allowOutside);
	}

	/**
	 * @param worldPos XYZ position
	 * @see WorldMouse#screen(double, double, double, boolean)
	 */
	@Nullable
	public Vec2f screen(Position worldPos) {
		return screen(worldPos.x(), worldPos.y(), worldPos.z(), false);
	}

	/**
	 * Convert screen coordinates to world position. Use {@link WorldMouse#clip()} if you only care about current mouse position
	 *
	 * @param x screen coordinate x-position
	 * @param y screen coordinate y-position
	 * @return a {@link Vec3} containing the screen coordinates in world position
	 */
	public Vec3 world(float x, float y) {
		var v = new Vector4f(x * 2F / width - 1F, -(y * 2F / height - 1F), 1F, 1F);
		v.mul(invertedWorldMatrix);
		v.div(v.w);
		return new Vec3(v.x + cameraPos.x, v.y + cameraPos.y, v.z + cameraPos.z);
	}
}
