package dev.latvian.mods.klib.particle;

import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.render.SubmitContext;
import dev.latvian.mods.klib.render.Submittable;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LineParticle extends CustomParticle {
	public static class State implements Submittable {
		public double x;
		public double y;
		public double z;
		public float sizeX;
		public float sizeY;
		public float sizeZ;
		public int startColor;
		public int endColor;

		@Override
		public void submit(SubmitContext ctx) {
			ctx.push(x, y, z);
			ctx.submit(RenderTypes.linesTranslucentNoDepthWrite(), (pose, buffer) -> {
				var callback = pose.transform(buffer);
				float len = (float) Math.sqrt(sizeX * sizeX + sizeY * sizeY + sizeZ * sizeZ);
				float nx = sizeX / len;
				float ny = sizeY / len;
				float nz = sizeZ / len;
				callback.acceptPos(0F, 0F, 0F).acceptCol(startColor).acceptNormal(nx, ny, nz).acceptLineWidth(ctx.lineWidth());
				callback.acceptPos(sizeX, sizeY, sizeZ).acceptCol(endColor).acceptNormal(nx, ny, nz).acceptLineWidth(ctx.lineWidth());
			});
			ctx.pop();
		}
	}

	private final LineParticleOptions options;
	public int prevAge;
	public final float sizeX;
	public final float sizeY;
	public final float sizeZ;

	private State state;

	public LineParticle(LineParticleOptions options, ClientLevel level, double x, double y, double z, double vx, double vy, double vz, RandomSource random) {
		super(level, x, y, z);
		this.hasPhysics = false;
		this.xd = 0D;
		this.yd = 0D;
		this.zd = 0D;
		this.options = options;
		this.sizeX = (float) vx;
		this.sizeY = (float) vy;
		this.sizeZ = (float) vz;
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

		double endX = state.x + sizeX;
		double endY = state.y + sizeY;
		double endZ = state.z + sizeZ;

		if (!frustum.isVisible(
			Math.min(state.x, endX),
			Math.min(state.y, endY),
			Math.min(state.z, endZ),
			Math.max(state.x, endX),
			Math.max(state.y, endY),
			Math.max(state.z, endZ)
		)) {
			return null;
		}

		float time = KMath.lerp(delta, prevAge, age) / (float) lifetime;

		state.sizeX = sizeX;
		state.sizeY = sizeY;
		state.sizeZ = sizeZ;
		state.startColor = options.startColor().value().get(time).argb();
		state.endColor = options.endColor().value().get(time).argb();

		return state;
	}
}
