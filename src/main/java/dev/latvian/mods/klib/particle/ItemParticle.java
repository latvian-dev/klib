package dev.latvian.mods.klib.particle;

import dev.latvian.mods.klib.icon.renderer.ItemIconRenderState;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.render.Submittable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ItemParticle extends CustomParticle {
	private final ItemParticleOptions options;

	private ItemIconRenderState state;

	public ItemParticle(ItemParticleOptions options, ClientLevel level, double x, double y, double z, double vx, double vy, double vz, RandomSource random) {
		super(level, x, y, z);
		this.xd = vx;
		this.yd = vy;
		this.zd = vz;
		this.options = options;
		setLifetime(Math.max(1, Math.abs(options.lifespan())));
		this.gravity = options.gravity();
	}

	@Override
	@Nullable
	public Submittable extract(Frustum frustum, Camera camera, Vec3 cameraPos, float delta) {
		if (options.item().isEmpty()) {
			return null;
		}

		if (state == null) {
			state = new ItemIconRenderState();
		}

		state.x = KMath.lerp(delta, xo, x);
		state.y = KMath.lerp(delta, yo, y);
		state.z = KMath.lerp(delta, zo, z);

		if (ItemIconRenderState.extract(Minecraft.getInstance(), options.item(), state, null) != null) {
			state.outlineColor = 0;
			state.scale = options.scale();
			state.light = getLightCoords(delta);
			return state;
		}

		return null;
	}
}
