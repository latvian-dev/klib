package dev.latvian.mods.klib.visual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.knumber.KNumber;
import dev.latvian.mods.klib.kvector.BlockPosKVector;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.shape.PositionedColoredShape;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

public record ShapeVisual(PositionedColoredShape shape, boolean outerLines, Ref<KNumber> progress) implements Visual {
	public static final DynamicType<RegistryFriendlyByteBuf, Visual> TYPE = DynamicType.create(
		"shape",
		RecordCodecBuilder.mapCodec(i -> i.group(
			PositionedColoredShape.MAP_CODEC.forGetter(ShapeVisual::shape),
			Codec.BOOL.optionalFieldOf("outer_lines", false).forGetter(ShapeVisual::outerLines),
			KNumber.CODEC.optionalFieldOf("progress", KNumber.PROGRESS).forGetter(ShapeVisual::progress)
		).apply(i, ShapeVisual::new)),
		CompositeStreamCodec.of(
			PositionedColoredShape.STREAM_CODEC, ShapeVisual::shape,
			ByteBufCodecs.BOOL, ShapeVisual::outerLines,
			KLibStreamCodecs.optional(KNumber.STREAM_CODEC, KNumber.PROGRESS), ShapeVisual::progress,
			ShapeVisual::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, Visual> type() {
		return TYPE;
	}

	@Override
	public boolean containsBlock(Vec3i block) {
		return shape.position().value() instanceof BlockPosKVector vec && vec.pos().equals(block);
	}
}
