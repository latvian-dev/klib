package dev.latvian.mods.klib.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.gradient.Gradient;
import dev.latvian.mods.klib.knumber.FadeOutKNumber;
import dev.latvian.mods.klib.knumber.KNumber;
import dev.latvian.mods.klib.kvector.KVector;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.shape.CubeShape;
import dev.latvian.mods.klib.shape.Shape;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.EasingType;

public record ShapeParticleOptions(
	int lifespan,
	Ref<Shape> shape,
	Ref<Gradient> quads,
	Ref<Gradient> lines,
	Ref<Gradient> outerLines,
	Ref<KVector> scale,
	Ref<KNumber> alpha
) implements ParticleOptions {
	public static final Ref<KNumber> DEFAULT_ALPHA = new FadeOutKNumber(KNumber.PROGRESS, KNumber.HALF, EasingType.LINEAR).ref();

	public static final MapCodec<ShapeParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		KLibCodecs.TICKS.optionalFieldOf("lifespan", 40).forGetter(ShapeParticleOptions::lifespan),
		Shape.CODEC.optionalFieldOf("shape", CubeShape.UNIT_CUBE).forGetter(ShapeParticleOptions::shape),
		Gradient.CODEC.optionalFieldOf("quads", Gradient.CYAN).forGetter(ShapeParticleOptions::quads),
		Gradient.CODEC.optionalFieldOf("lines", Gradient.WHITE).forGetter(ShapeParticleOptions::lines),
		Gradient.CODEC.optionalFieldOf("outer_lines", Gradient.EMPTY).forGetter(ShapeParticleOptions::outerLines),
		KVector.CODEC.optionalFieldOf("scale", KVector.ONE).forGetter(ShapeParticleOptions::scale),
		KNumber.CODEC.optionalFieldOf("alpha", DEFAULT_ALPHA).forGetter(ShapeParticleOptions::alpha)
	).apply(instance, ShapeParticleOptions::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ShapeParticleOptions> STREAM_CODEC = CompositeStreamCodec.of(
		ByteBufCodecs.VAR_INT, ShapeParticleOptions::lifespan,
		Shape.STREAM_CODEC, ShapeParticleOptions::shape,
		Gradient.STREAM_CODEC, ShapeParticleOptions::quads,
		Gradient.STREAM_CODEC, ShapeParticleOptions::lines,
		Gradient.STREAM_CODEC, ShapeParticleOptions::outerLines,
		KVector.STREAM_CODEC, ShapeParticleOptions::scale,
		KLibStreamCodecs.optional(KNumber.STREAM_CODEC, DEFAULT_ALPHA), ShapeParticleOptions::alpha,
		ShapeParticleOptions::new
	);

	public ShapeParticleOptions(int lifespan, Ref<Shape> shape, Ref<Gradient> quadColor, Ref<Gradient> lineColor) {
		this(lifespan, shape, quadColor, lineColor, Gradient.EMPTY, KVector.ONE, DEFAULT_ALPHA);
	}

	public ShapeParticleOptions(int lifespan, Ref<Gradient> quadColor, Ref<Gradient> lineColor) {
		this(lifespan, CubeShape.UNIT_CUBE, quadColor, lineColor);
	}

	@Override
	public ParticleType<?> getType() {
		return KLibParticles.SHAPE.get();
	}
}
