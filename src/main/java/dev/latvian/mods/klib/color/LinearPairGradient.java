package dev.latvian.mods.klib.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record LinearPairGradient(Color start, Color end) implements Gradient {
	public static final Codec<LinearPairGradient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Color.CODEC.fieldOf("start").forGetter(LinearPairGradient::start),
		Color.CODEC.fieldOf("end").forGetter(LinearPairGradient::end)
	).apply(instance, LinearPairGradient::new));

	public static final StreamCodec<ByteBuf, LinearPairGradient> STREAM_CODEC = CompositeStreamCodec.of(
		Color.STREAM_CODEC, LinearPairGradient::start,
		Color.STREAM_CODEC, LinearPairGradient::end,
		LinearPairGradient::new
	);

	@Override
	public Color get(float delta) {
		/*
		if (delta < 0.5F) {
			return start.get(delta * 2F).lerp(delta, end.get(0F));
		} else {
			return start.get(1F).lerp(delta, end.get((delta - 0.5F) * 2F));
		}
		 */

		return start.lerp(delta, end);
	}

	@Override
	public Gradient optimize() {
		return start.equals(end) ? start : this;
	}

	@Override
	public List<PositionedColor> getPositionedColors() {
		return List.of(new PositionedColor(0F, start), new PositionedColor(1F, end));
	}
}
