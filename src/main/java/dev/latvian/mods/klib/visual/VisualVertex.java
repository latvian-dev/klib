package dev.latvian.mods.klib.visual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.gradient.Gradient;
import dev.latvian.mods.klib.kvector.KVector;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record VisualVertex(Ref<KVector> position, Ref<Gradient> color) {
	public static final MapCodec<VisualVertex> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		KVector.CODEC.fieldOf("position").forGetter(VisualVertex::position),
		Gradient.CODEC.optionalFieldOf("color", Gradient.WHITE).forGetter(VisualVertex::color)
	).apply(i, VisualVertex::new));

	public static final Codec<VisualVertex> CODEC = MAP_CODEC.codec();

	public static final StreamCodec<RegistryFriendlyByteBuf, VisualVertex> STREAM_CODEC = CompositeStreamCodec.of(
		KVector.STREAM_CODEC, VisualVertex::position,
		Gradient.STREAM_CODEC, VisualVertex::color,
		VisualVertex::new
	);
}
