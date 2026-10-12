package dev.latvian.mods.klib.codec;

import com.mojang.renderpearl.api.pipeline.BlendEquation;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.BlendOp;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Map;

public interface KLibClientCodecs {
	Codec<BlendFunction> NAMED_BLEND_FUNCTION = KLibCodecs.partialMap(Map.of(
		"lightning", BlendFunction.LIGHTNING,
		"glint", BlendFunction.GLINT,
		"overlay", BlendFunction.OVERLAY,
		"translucent", BlendFunction.TRANSLUCENT,
		"translucent_premultiplied_alpha", BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA,
		"additive", BlendFunction.ADDITIVE,
		"entity_outline_blit", BlendFunction.ENTITY_OUTLINE_BLIT,
		"invert", BlendFunction.INVERT,
		"max", BlendFunction.MAX
	), Codec.STRING, false);

	Codec<BlendFactor> BLEND_FACTOR = KLibCodecs.anyEnum(BlendFactor.values());
	Codec<BlendOp> BLEND_OP = KLibCodecs.anyEnum(BlendOp.values());

	Codec<BlendEquation> BLEND_EQUATION = RecordCodecBuilder.create(instance -> instance.group(
		BLEND_FACTOR.optionalFieldOf("src", BlendFunction.TRANSLUCENT.color().sourceFactor()).forGetter(BlendEquation::sourceFactor),
		BLEND_FACTOR.optionalFieldOf("dst", BlendFunction.TRANSLUCENT.color().destFactor()).forGetter(BlendEquation::destFactor),
		BLEND_OP.optionalFieldOf("op", BlendOp.ADD).forGetter(BlendEquation::op)
	).apply(instance, BlendEquation::new));

	Codec<BlendFunction> DIRECT_BLEND_FUNCTION = RecordCodecBuilder.create(instance -> instance.group(
		BLEND_EQUATION.optionalFieldOf("color", BlendFunction.TRANSLUCENT.color()).forGetter(BlendFunction::color),
		BLEND_EQUATION.optionalFieldOf("alpha", BlendFunction.TRANSLUCENT.alpha()).forGetter(BlendFunction::alpha)
	).apply(instance, BlendFunction::new));

	Codec<BlendFunction> BLEND_FUNCTION = KLibCodecs.or(List.of(NAMED_BLEND_FUNCTION, DIRECT_BLEND_FUNCTION));
}
