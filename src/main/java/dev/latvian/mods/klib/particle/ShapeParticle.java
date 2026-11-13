package dev.latvian.mods.klib.particle;

import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.gradient.Gradient;
import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.math.HitBuilder;
import dev.latvian.mods.klib.math.Identity;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.render.SubmitContext;
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
	public static class State implements CustomParticleState {
		private static final Shape HIT_SPHERE = new SphereShape(1F / 8F);

		public double x;
		public double y;
		public double z;
		public float scaleX;
		public float scaleY;
		public float scaleZ;
		public Shape shape;
		public Color quadColor;
		public Color lineColor;
		public Color outerLineColor;
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
				ctx.submitLines(HIT_SPHERE, Identity.VEC_3, Color.BLACK);
				ctx.pop();
			}
		}
	}

	private final ShapeParticleOptions options;
	private final Ref<Shape> shape;
	private final Ref<Gradient> quadColor;
	private final Ref<Gradient> lineColor;
	private final Ref<Gradient> outerLineColor;
	public int prevAge;

	private State state;

	public ShapeParticle(ShapeParticleOptions options, ClientLevel level, double x, double y, double z, double vx, double vy, double vz, RandomSource random) {
		super(level, x, y, z);
		this.xd = vx;
		this.yd = vy;
		this.zd = vz;
		this.options = options;
		this.shape = options.shape();
		this.quadColor = options.quads();
		this.lineColor = options.lines();
		this.outerLineColor = options.outerLines();
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
		if (!shape.value().isVisible(x, y, z, frustum)) {
			return null;
		}

		if (state == null) {
			state = new State();
		}

		float time = KMath.lerp(delta, prevAge, age);

		var ctx = new KNumberContext();
		ctx.updateLevelData(level);
		ctx.delta = delta;
		ctx.progress = time / (double) lifetime;

		float alpha = (float) options.alpha().ref().value().getOr(ctx, 1D);
		var scale = options.scale().value().get(ctx);

		if (scale == null) {
			scale = KMath.ONE_VEC3;
		}

		state.x = KMath.lerp(delta, xo, x);
		state.y = KMath.lerp(delta, yo, y);
		state.z = KMath.lerp(delta, zo, z);
		state.scaleX = (float) scale.x;
		state.scaleY = (float) scale.y;
		state.scaleZ = (float) scale.z;
		state.shape = options.shape().value();
		state.quadColor = quadColor.value().get(time / (float) lifetime).mixAlpha(alpha * 0.2F);
		state.lineColor = lineColor.value().get(time / (float) lifetime).mixAlpha(alpha);
		state.outerLineColor = outerLineColor.value().get(time / (float) lifetime).mixAlpha(alpha);
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
