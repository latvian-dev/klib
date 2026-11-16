package dev.latvian.mods.klib.particle;

import com.mojang.math.Axis;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.render.SubmitContext;
import dev.latvian.mods.klib.render.Submittable;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TextParticle extends CustomParticle {
	public static class State implements Submittable {
		public double x;
		public double y;
		public double z;
		public int color;
		public int backgroundColor;
		public float scale;
		public float distance;
		public boolean seeThrough;
		public boolean shadow;
		public List<Display.TextDisplay.CachedLine> textLines;
		public int maxLineWidth;
		public int light;

		@Override
		public void submit(SubmitContext ctx) {
			ctx.push(x, y, z);
			ctx.scale(-0.025F * scale);
			ctx.rotate(ctx.mc().gameRenderer.mainCamera().rotation());
			ctx.rotate(Axis.YP.rotationDegrees(180F));

			var textDisplayMode = seeThrough ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET;

			float y = 0.0F;
			var pose = ctx.poseStack().last().pose();
			int lineSpacing = 1;
			int lineHeight = 9 + 1;
			int height = textLines.size() * lineHeight - 1;
			pose.translate(1F - maxLineWidth / 2F, -height, 0F);

			if (backgroundColor != 0) {
				ctx.collector().submitTextBackground(ctx.poseStack(), -1F, -1F, maxLineWidth, height, backgroundColor, textDisplayMode, light);
			}

			for (var line : textLines) {
				float offset = maxLineWidth / 2F - line.width() / 2F;
				ctx.collector().submitText(ctx.poseStack(), offset, y, line.contents(), shadow, textDisplayMode, light, color, 0, 0);
				y += lineHeight;
			}

			ctx.pop();
		}
	}

	private final TextParticleOptions options;
	public int prevAge;
	public List<Display.TextDisplay.CachedLine> text;
	public int maxLineWidth;

	private State state;

	public TextParticle(TextParticleOptions options, ClientLevel level, double x, double y, double z, double vx, double vy, double vz, RandomSource random) {
		super(level, x, y, z);
		this.hasPhysics = false;
		this.xd = vx;
		this.yd = vy;
		this.zd = vz;
		this.options = options;
		setLifetime(Math.max(1, Math.abs(options.lifespan())));

		var lines = level.minecraft.font.split(options.text(), options.lineWidth());
		this.text = new ArrayList<>(lines.size());
		this.maxLineWidth = 0;

		for (FormattedCharSequence line : lines) {
			int lineWidth = level.minecraft.font.width(line);
			maxLineWidth = Math.max(maxLineWidth, lineWidth);
			this.text.add(new Display.TextDisplay.CachedLine(line, lineWidth));
		}
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

		float time = KMath.lerp(delta, prevAge, age) / (float) lifetime;

		state.color = options.color().value().get(time).argb();

		if (ARGB.alpha(state.color) == 0) {
			return null;
		}

		state.backgroundColor = options.backgroundColor().value().get(time).argb();
		state.distance = options.distance();
		state.scale = options.scale();
		state.seeThrough = options.seeThrough();
		state.shadow = options.shadow();

		state.textLines = text;
		state.maxLineWidth = maxLineWidth;
		state.light = LightCoordsUtil.FULL_BRIGHT;

		return state;
	}
}
