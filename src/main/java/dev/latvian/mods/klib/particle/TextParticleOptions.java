package dev.latvian.mods.klib.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.gradient.Gradient;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TextParticleOptions(
	int lifespan,
	Component text,
	Ref<Gradient> color,
	Ref<Gradient> backgroundColor,
	int lineWidth,
	float distance,
	float scale,
	boolean seeThrough,
	boolean shadow
) implements ParticleOptions {
	public static final MapCodec<TextParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		KLibCodecs.TICKS.optionalFieldOf("lifespan", 40).forGetter(TextParticleOptions::lifespan),
		ComponentSerialization.CODEC.fieldOf("text").forGetter(TextParticleOptions::text),
		Gradient.CODEC.optionalFieldOf("color", Gradient.WHITE).forGetter(TextParticleOptions::color),
		Gradient.CODEC.optionalFieldOf("background_color", Gradient.EMPTY).forGetter(TextParticleOptions::backgroundColor),
		Codec.INT.optionalFieldOf("line_width", 200).forGetter(TextParticleOptions::lineWidth),
		Codec.FLOAT.optionalFieldOf("distance", 16F).forGetter(TextParticleOptions::distance),
		Codec.FLOAT.optionalFieldOf("scale", 1F).forGetter(TextParticleOptions::scale),
		Codec.BOOL.optionalFieldOf("see_through", false).forGetter(TextParticleOptions::seeThrough),
		Codec.BOOL.optionalFieldOf("shadow", false).forGetter(TextParticleOptions::shadow)
	).apply(instance, TextParticleOptions::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, TextParticleOptions> STREAM_CODEC = CompositeStreamCodec.of(
		ByteBufCodecs.VAR_INT, TextParticleOptions::lifespan,
		ComponentSerialization.TRUSTED_STREAM_CODEC, TextParticleOptions::text,
		Gradient.STREAM_CODEC, TextParticleOptions::color,
		Gradient.STREAM_CODEC, TextParticleOptions::backgroundColor,
		ByteBufCodecs.VAR_INT, TextParticleOptions::lineWidth,
		ByteBufCodecs.FLOAT, TextParticleOptions::distance,
		ByteBufCodecs.FLOAT, TextParticleOptions::scale,
		ByteBufCodecs.BOOL, TextParticleOptions::seeThrough,
		ByteBufCodecs.BOOL, TextParticleOptions::shadow,
		TextParticleOptions::new
	);

	public TextParticleOptions(int lifespan, Component text) {
		this(lifespan, text, Gradient.WHITE, Gradient.EMPTY, 200, 16F, 1F, false, false);
	}

	@Override
	public ParticleType<?> getType() {
		return KLibParticles.TEXT.get();
	}
}
