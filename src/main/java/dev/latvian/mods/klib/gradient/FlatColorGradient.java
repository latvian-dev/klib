package dev.latvian.mods.klib.gradient;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.color.PositionedColor;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.RandomSource;

import java.util.List;

public record FlatColorGradient(Color color) implements Gradient {
	public static final DynamicType<ByteBuf, Gradient> TYPE = DynamicType.create(
		"color",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Color.CODEC.fieldOf("color").forGetter(FlatColorGradient::color)
		).apply(instance, FlatColorGradient::new)),
		CompositeStreamCodec.of(
			Color.STREAM_CODEC, FlatColorGradient::color,
			FlatColorGradient::new
		)
	);

	@Override
	public CustomRegistryType<ByteBuf, Gradient> type() {
		return color.argb() == 0 ? EMPTY : TYPE;
	}

	@Override
	public Color get(float delta) {
		return color;
	}

	@Override
	public Color sample(RandomSource random) {
		return color;
	}

	@Override
	public List<PositionedColor> getPositionedColors() {
		return List.of(new PositionedColor(0F, color));
	}
}
