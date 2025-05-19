package dev.latvian.mods.kmath;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

/**
 * @author Lat
 */
public record WorldMouse(
	Matrix4fc worldMatrix,
	Matrix4fc invertedWorldMatrix,
	Vec3 cameraPos,
	float width,
	float height,
	Vec2f screenPos,
	Vec3 worldPos,
	@Nullable BlockHitResult hit,
	@Nullable BlockPos pos,
	@Nullable BlockPos altPos
) {
	public static WorldMouse clip(Minecraft mc, Vec3 cameraPos, Matrix4fc worldMatrix, double maxDistance, @Nullable Vec2f screenPos) {
		var invertedWorldMatrix = new Matrix4f(worldMatrix).invert();
		var width = mc.getWindow().getGuiScaledWidth();
		var height = mc.getWindow().getGuiScaledHeight();

		if (screenPos == null) {
			if (mc.screen != null) {
				screenPos = new Vec2f(
					(float) (mc.mouseHandler.xpos() * width / (double) mc.getWindow().getWidth()),
					(float) (mc.mouseHandler.ypos() * height / (double) mc.getWindow().getHeight())
				);
			} else {
				screenPos = new Vec2f(width * 0.5F, height * 0.5F);
			}
		}

		var worldPos = world(invertedWorldMatrix, cameraPos, width, height, screenPos.x(), screenPos.y());

		var dist = cameraPos.distanceTo(worldPos);
		var lerp = Math.min(1D, maxDistance / dist);

		var hit = mc.level == null || mc.player == null ? null : mc.level.clip(new ClipContext(
			cameraPos,
			cameraPos.lerp(worldPos, lerp),
			ClipContext.Block.OUTLINE,
			ClipContext.Fluid.SOURCE_ONLY,
			mc.player
		));

		if (hit != null && hit.getType() == HitResult.Type.MISS) {
			hit = null;
		}

		var pos = hit == null ? null : hit.getBlockPos();
		var altPos = pos == null ? null : Screen.hasAltDown() ? pos.relative(hit.getDirection()) : pos;

		return new WorldMouse(
			worldMatrix,
			invertedWorldMatrix,
			cameraPos,
			width,
			height,
			screenPos,
			worldPos,
			hit,
			pos,
			altPos
		);
	}

	public static WorldMouse clip(Minecraft mc, Vec3 cameraPos, Matrix4fc worldMatrix) {
		return clip(mc, cameraPos, worldMatrix, 1000D, null);
	}

	@Nullable
	public static Vec2f screen(Matrix4fc worldMatrix, Vec3 camera, float width, float height, double worldX, double worldY, double worldZ, boolean allowOutside) {
		var v = new Vector4f((float) (worldX - camera.x), (float) (worldY - camera.y), (float) (worldZ - camera.z), 1F);
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

	public static Vec3 world(Matrix4fc invertedWorldMatrix, Vec3 camera, float width, float height, float x, float y) {
		var v = new Vector4f(x * 2F / width - 1F, -(y * 2F / height - 1F), 1F, 1F);
		v.mul(invertedWorldMatrix);
		v.div(v.w);
		return new Vec3(v.x + camera.x, v.y + camera.y, v.z + camera.z);
	}

	/**
	 * The current coordinate of the mouse in world coordinates
	 */
	@Override
	public Vec3 worldPos() {
		return worldPos;
	}

	/**
	 * Raycast block hit result
	 */
	@Override
	@Nullable
	public BlockHitResult hit() {
		return hit;
	}

	/**
	 * Block position of the hit
	 */
	@Override
	@Nullable
	public BlockPos pos() {
		return pos;
	}

	/**
	 * Block position of the hit, offset to hit side if Alt key is held down
	 */
	@Override
	@Nullable
	public BlockPos altPos() {
		return altPos;
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
		return screen(worldMatrix, cameraPos, width, height, worldX, worldY, worldZ, allowOutside);
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
	 * Convert screen coordinates to world position. Use {@link WorldMouse#worldPos} if you only care about current mouse position
	 *
	 * @param x screen coordinate x-position
	 * @param y screen coordinate y-position
	 * @return a {@link Vec3} containing the screen coordinates in world position
	 */
	public Vec3 world(float x, float y) {
		return world(invertedWorldMatrix, cameraPos, width, height, x, y);
	}
}
