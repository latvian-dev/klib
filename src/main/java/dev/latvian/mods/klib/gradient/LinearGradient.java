package dev.latvian.mods.klib.gradient;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.color.PositionedColor;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.util.ID;
import io.netty.buffer.ByteBuf;

import java.util.List;

public record LinearGradient(Color start, Color end) implements Gradient {
	public static final CustomRegistryType<ByteBuf, Gradient> TYPE = Gradient.REGISTRY.dynamic(ID.klib("linear"),
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Color.CODEC.fieldOf("start").forGetter(LinearGradient::start),
			Color.CODEC.fieldOf("end").forGetter(LinearGradient::end)
		).apply(instance, LinearGradient::new)),
		CompositeStreamCodec.of(
			Color.STREAM_CODEC, LinearGradient::start,
			Color.STREAM_CODEC, LinearGradient::end,
			LinearGradient::new
		)
	);

	@Override
	public CustomRegistryType<ByteBuf, Gradient> type() {
		return TYPE;
	}

	@Override
	public Color get(float delta) {
		return start.lerp(delta, end);
	}

	@Override
	public Gradient optimize() {
		return start.equals(end) ? start.toGradient() : this;
	}

	@Override
	public List<PositionedColor> getPositionedColors() {
		return List.of(new PositionedColor(0F, start), new PositionedColor(1F, end));
	}
}
