package dev.latvian.mods.klib.math;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4d;
import org.joml.Vector4f;

public record WorldMouse(
	Minecraft mc,
	Vec3 cameraPos,
	float width,
	float height,
	Vec2d defaultScreenPos
) {
	public static WorldMouse of(Minecraft mc, Vec3 cameraPos) {
		var width = mc.getWindow().getGuiScaledWidth();
		var height = mc.getWindow().getGuiScaledHeight();

		return new WorldMouse(
			mc,
			cameraPos,
			width,
			height,
			mc.screen == null ? new Vec2d(
				width * 0.5D,
				height * 0.5D
			) : new Vec2d(
				mc.mouseHandler.xpos() * width / (double) mc.getWindow().getWidth(),
				mc.mouseHandler.ypos() * height / (double) mc.getWindow().getHeight()
			)
		);
	}

	@Nullable
	public Cursor clip(double maxDistance, ClipContext.Block blockClipContext, ClipContext.Fluid fluidClipContext, @Nullable Vec2d screenPos, @Nullable Entity clipEntity) {
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
		double rx = worldX - cameraPos.x;
		double ry = worldY - cameraPos.y;
		double rz = worldZ - cameraPos.z;
		double len = Math.sqrt(rx * rx + ry * ry + rz * rz);

		var v = new Vector4f((float) (rx / len), (float) (ry / len), (float) (rz / len), 1F);
		v.mul(ClientMatrices.WORLD);
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
	 * Convert screen coordinates to world position. Use {@link WorldMouse#clip(double, ClipContext.Block, ClipContext.Fluid, Vec2d, Entity)} if you only care about current mouse position
	 *
	 * @param x screen coordinate x-position
	 * @param y screen coordinate y-position
	 * @return a {@link Vec3} containing the screen coordinates in world position
	 */
	public Vec3 world(double x, double y) {
		var v = new Vector4d(x * 2D / width - 1D, -(y * 2D / height - 1F), 1D, 1D);
		v.mul(ClientMatrices.INVERSE_WORLD);
		v.div(v.w);
		return new Vec3(v.x + cameraPos.x, v.y + cameraPos.y, v.z + cameraPos.z);
	}
}
