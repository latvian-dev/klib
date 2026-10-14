package dev.latvian.mods.klib.codec;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.backport.BlendEquation;
import dev.latvian.mods.klib.backport.BlendFactor;
import dev.latvian.mods.klib.backport.BlendOp;

import java.util.List;
import java.util.Map;

public interface KLibClientCodecs {
	Codec<BlendFunction> NAMED_BLEND_FUNCTION = KLibCodecs.partialMap(Map.of(
		"lightning", BlendFunction.LIGHTNING,
		"glint", BlendFunction.GLINT,
		"overlay", BlendFunction.OVERLAY,
		"translucent", BlendFunction.TRANSLUCENT,
		"additive", BlendFunction.ADDITIVE,
		"panorama", BlendFunction.PANORAMA,
		"entity_outline_blit", BlendFunction.ENTITY_OUTLINE_BLIT
	), Codec.STRING, false);

	Codec<BlendFactor> BLEND_FACTOR = KLibCodecs.anyEnum(BlendFactor.values());
	Codec<BlendOp> BLEND_OP = KLibCodecs.anyEnum(BlendOp.values());

	Codec<BlendEquation> BLEND_EQUATION = RecordCodecBuilder.create(instance -> instance.group(
		BLEND_FACTOR.optionalFieldOf("src", BlendFactor.SRC_ALPHA).forGetter(BlendEquation::sourceFactor),
		BLEND_FACTOR.optionalFieldOf("dst", BlendFactor.ONE_MINUS_SRC_ALPHA).forGetter(BlendEquation::destFactor),
		BLEND_OP.optionalFieldOf("op", BlendOp.ADD).forGetter(BlendEquation::op)
	).apply(instance, BlendEquation::new));

	Codec<BlendFunction> DIRECT_BLEND_FUNCTION = RecordCodecBuilder.create(instance -> instance.group(
		BLEND_EQUATION.optionalFieldOf("color", new BlendEquation(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA)).forGetter(func -> new BlendEquation(func.sourceColor(), func.destColor())),
		BLEND_EQUATION.optionalFieldOf("alpha", new BlendEquation(BlendFactor.ONE, BlendFactor.ONE_MINUS_SRC_ALPHA)).forGetter(func -> new BlendEquation(func.sourceAlpha(), func.destAlpha()))
	).apply(instance, (color, alpha) -> new BlendFunction(color.sourceFactor().src, color.sourceFactor().dst, alpha.sourceFactor().src, alpha.sourceFactor().dst)));

	Codec<BlendFunction> BLEND_FUNCTION = KLibCodecs.or(List.of(NAMED_BLEND_FUNCTION, DIRECT_BLEND_FUNCTION));
}
