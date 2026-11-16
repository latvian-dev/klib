package dev.latvian.mods.klib.particle;

import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.math.HitBuilder;
import dev.latvian.mods.klib.math.Identity;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.render.SubmitContext;
import dev.latvian.mods.klib.render.Submittable;
import dev.latvian.mods.klib.shape.Shape;
import dev.latvian.mods.klib.shape.SphereShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ShapeParticle extends CustomParticle {
	public static class State implements Submittable {
		private static final Shape HIT_SPHERE = new SphereShape(1F / 8F);

		public double x;
		public double y;
		public double z;
		public float scaleX;
		public float scaleY;
		public float scaleZ;
		public Shape shape;
		public int quadColor;
		public int lineColor;
		public int outerLineColor;
		public Vec3 hit;

		@Override
		public void submit(SubmitContext ctx) {
			ctx.push(x, y, z);
			ctx.poseStack().scale(scaleX, scaleY, scaleZ);
			ctx.submitQuads(shape, Identity.VEC_3, quadColor);
			ctx.submitLines(shape, Identity.VEC_3, lineColor);
			ctx.submitOuterLines(shape, Identity.VEC_3, outerLineColor);
			ctx.pop();

			if (hit != null) {
				ctx.push(hit);
				ctx.submitLines(HIT_SPHERE, Identity.VEC_3, 0xFF000000);
				ctx.pop();
			}
		}
	}

	private final ShapeParticleOptions options;
	public int prevAge;

	private State state;

	public ShapeParticle(ShapeParticleOptions options, ClientLevel level, double x, double y, double z, double vx, double vy, double vz, RandomSource random) {
		super(level, x, y, z);
		this.xd = vx;
		this.yd = vy;
		this.zd = vz;
		this.options = options;
		setLifetime(Math.max(1, Math.abs(options.lifespan())));
	}

	@Override
	public void tick() {
		prevAge = age;
		super.tick();
	}

	@Override
	@Nullable
	public State extract(Frustum frustum, Camera camera, Vec3 cameraPos, float delta) {
		if (state == null) {
			state = new State();
		}

		state.x = KMath.lerp(delta, xo, x);
		state.y = KMath.lerp(delta, yo, y);
		state.z = KMath.lerp(delta, zo, z);

		if (!options.shape().value().isVisible(state.x, state.y, state.z, frustum)) {
			return null;
		}

		float time = KMath.lerp(delta, prevAge, age);
		float dtime = time / (float) lifetime;

		var ctx = new KNumberContext();
		ctx.updateLevelData(level);
		ctx.delta = delta;
		ctx.progress = (double) dtime;

		float alpha = (float) options.alpha().ref().value().getOr(ctx, 1D);
		var scale = options.scale().value().get(ctx);

		if (scale == null) {
			scale = KMath.ONE_VEC3;
		}

		state.scaleX = (float) scale.x;
		state.scaleY = (float) scale.y;
		state.scaleZ = (float) scale.z;
		state.shape = options.shape().value();
		state.quadColor = options.quads().value().get(dtime).mixAlpha(alpha * 0.2F).argb();
		state.lineColor = options.lines().value().get(dtime).mixAlpha(alpha).argb();
		state.outerLineColor = options.outerLines().value().get(dtime).mixAlpha(alpha).argb();
		state.hit = null;

		var mc = Minecraft.getInstance();

		if (mc.player != null && mc.getDebugOverlay().showDebugScreen()) {
			var vecf = camera.forwardVector().mul((float) mc.player.blockInteractionRange(), new Vector3f());
			var hit = new HitBuilder(camera.position(), camera.position().add(vecf.x, vecf.y, vecf.z));

			if (state.shape.hit(new Vec3(state.x, state.y, state.z), hit)) {
				state.hit = hit.hit;
			}
		}

		return state;
	}
}
