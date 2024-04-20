package dev.latvian.mods.kmath.util;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * @author Lat
 */
public class WorldMouse implements WorldRenderEvents.AfterSetup {
	private static WorldMouse instance;

	public static WorldMouse get() {
		if (instance == null) {
			instance = new WorldMouse();
			WorldRenderEvents.AFTER_SETUP.register(instance);
		}

		return instance;
	}

	public WorldRenderContext context;
	public Matrix4f worldMatrix = new Matrix4f();
	public Matrix4f invertedWorldMatrix = new Matrix4f();
	public float scaledWidth = 1F;
	public float scaledHeight = 1F;

	/**
	 * The current coordinate of the mouse in screen coordinates
	 */
	public final Vector2f screen = new Vector2f(0.5F, 0.5F);

	/**
	 * The current coordinate of the mouse in world coordinates
	 */
	public Vec3d world = Vec3d.ZERO;

	/**
	 * Raycast block hit result
	 */
	private BlockHitResult hit = null;

	/**
	 * Block position of the hit
	 */
	private BlockPos pos = null;

	/**
	 * Block position of the hit, offset to hit side if Alt key is held down
	 */
	private BlockPos altPos = null;

	/**
	 * Each frame, {@link WorldMouse} is marked as dirty. If a request for data is sent during a dirty frame, {@link WorldMouse} will
	 * re-poll raycasting data. This is done because raycasting each frame is expensive (>=50% of tick in some scenarios).
	 */
	private boolean updateHit = true;

	// Each frame: updated stored context.
	@Override
	public void afterSetup(WorldRenderContext ctx) {
		context = ctx;

		var mc = MinecraftClient.getInstance();
		worldMatrix.set(context.projectionMatrix());
		worldMatrix.mul(context.matrixStack().peek().getPositionMatrix());
		invertedWorldMatrix.set(worldMatrix);
		invertedWorldMatrix.invert();
		scaledWidth = mc.getWindow().getScaledWidth();
		scaledHeight = mc.getWindow().getScaledHeight();

		if (mc.currentScreen != null) {
			screen.set(mc.mouse.getX() * scaledWidth / (double) mc.getWindow().getWidth(), mc.mouse.getY() * scaledHeight / (double) mc.getWindow().getHeight());
		} else {
			screen.set(0.5D * scaledWidth, 0.5D * scaledHeight);
		}

		world = world(screen.x, screen.y);
		updateHit = true;
	}

	private WorldMouse updateHit() {
		if (updateHit) {
			updateHit = false;
			hit = null;
			pos = null;
			altPos = null;

			if (context != null) {
				var mc = MinecraftClient.getInstance();

				var cameraPos = context.camera().getPos();
				var wpos = world.add(cameraPos);
				var dist = cameraPos.distanceTo(wpos);
				var lerp = Math.min(1D, 1000D / dist);

				hit = mc.world.raycast(new RaycastContext(
					cameraPos,
					cameraPos.lerp(wpos, lerp),
					RaycastContext.ShapeType.OUTLINE,
					RaycastContext.FluidHandling.SOURCE_ONLY,
					mc.player
				));

				if (hit != null && hit.getType() == HitResult.Type.MISS) {
					hit = null;
				}

				pos = hit == null ? null : hit.getBlockPos();
				altPos = pos == null ? null : Screen.hasAltDown() ? pos.offset(hit.getSide()) : pos;
			}
		}

		return this;
	}

	/**
	 * Convert world position to screen coordinates
	 *
	 * @param worldPosX    X position
	 * @param worldPosY    Y position
	 * @param worldPosZ    Z position
	 * @param allowOutside Allow outside the screen
	 * @return Screen coordinates, or null if outside the screen
	 */
	@Nullable
	public Vector2f screen(double worldPosX, double worldPosY, double worldPosZ, boolean allowOutside) {
		var c = context.camera().getPos();
		var v = new Vector4f((float) (worldPosX - c.x), (float) (worldPosY - c.y), (float) (worldPosZ - c.z), 1F);
		v.mul(worldMatrix);
		v.div(v.w);

		if (allowOutside || v.z > 0F && v.z < 1F) {
			return new Vector2f(
				(0.5F + v.x * 0.5F) * scaledWidth,
				(0.5F - v.y * 0.5F) * scaledHeight
			);
		}

		return null;
	}

	/**
	 * @param worldPosX X position
	 * @param worldPosY Y position
	 * @param worldPosZ Z position
	 * @see WorldMouse#screen(double, double, double, boolean)
	 */
	@Nullable
	public Vector2f screen(double worldPosX, double worldPosY, double worldPosZ) {
		return screen(worldPosX, worldPosY, worldPosZ, false);
	}

	/**
	 * @param worldPos     XYZ position
	 * @param allowOutside Allow outside the screen
	 * @see WorldMouse#screen(double, double, double, boolean)
	 */
	@Nullable
	public Vector2f screen(Position worldPos, boolean allowOutside) {
		return screen(worldPos.getX(), worldPos.getY(), worldPos.getZ(), allowOutside);
	}

	/**
	 * @param worldPos XYZ position
	 * @see WorldMouse#screen(double, double, double, boolean)
	 */
	@Nullable
	public Vector2f screen(Position worldPos) {
		return screen(worldPos.getX(), worldPos.getY(), worldPos.getZ(), false);
	}

	/**
	 * Convert screen coordinates to world position. Use {@link WorldMouse#world} if you only care about current mouse position
	 *
	 * @param x screen coordinate x-position
	 * @param y screen coordinate y-position
	 * @return a {@link Vec3d} containing the screen coordinates in world position
	 */
	public Vec3d world(double x, double y) {
		var v = new Vector4f((float) (x * 2D / scaledWidth - 1D), (float) (-(y * 2D / scaledHeight - 1D)), 1F, 1F);
		v.mul(invertedWorldMatrix);
		v.div(v.w);
		return new Vec3d(v.x, v.y, v.z);
	}

	public BlockHitResult hit() {
		return updateHit().hit;
	}

	public BlockPos pos() {
		return updateHit().pos;
	}

	public BlockPos altPos() {
		return updateHit().altPos;
	}
}
