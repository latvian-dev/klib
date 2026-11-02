package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.knumber.KNumber;
import dev.latvian.mods.klib.kvector.KVector;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.visual.ShapeVisual;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PositionedColoredShape(Ref<KVector> position, ColoredShape shape) {
	public static final MapCodec<PositionedColoredShape> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		KVector.CODEC.fieldOf("position").forGetter(PositionedColoredShape::position),
		ColoredShape.MAP_CODEC.forGetter(PositionedColoredShape::shape)
	).apply(instance, PositionedColoredShape::new));

	public static final Codec<PositionedColoredShape> CODEC = MAP_CODEC.codec();

	public static final StreamCodec<RegistryFriendlyByteBuf, PositionedColoredShape> STREAM_CODEC = CompositeStreamCodec.of(
		KVector.STREAM_CODEC, PositionedColoredShape::position,
		ColoredShape.STREAM_CODEC, PositionedColoredShape::shape,
		PositionedColoredShape::new
	);

	public ShapeVisual asVisual(boolean outerLines, Ref<KNumber> progress) {
		return new ShapeVisual(this, outerLines, progress);
	}

	public ShapeVisual asVisual() {
		return asVisual(false, KNumber.PROGRESS);
	}
}
