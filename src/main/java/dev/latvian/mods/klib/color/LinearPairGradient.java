package dev.latvian.mods.klib.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record LinearPairGradient(Gradient start, Gradient end) implements Gradient {
	public static final Codec<LinearPairGradient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Gradient.CODEC.fieldOf("start").forGetter(LinearPairGradient::start),
		Gradient.CODEC.fieldOf("end").forGetter(LinearPairGradient::end)
	).apply(instance, LinearPairGradient::new));

	public static final StreamCodec<ByteBuf, LinearPairGradient> STREAM_CODEC = StreamCodec.composite(
		Gradient.STREAM_CODEC, LinearPairGradient::start,
		Gradient.STREAM_CODEC, LinearPairGradient::end,
		LinearPairGradient::new
	);

	@Override
	public Color get(float delta) {
		if (delta < 0.5F) {
			return start.get(delta * 2F).lerp(delta, end.get(0F));
		} else {
			return start.get(1F).lerp(delta, end.get((delta - 0.5F) * 2F));
		}
	}

	@Override
	public Gradient resolve() {
		return start.equals(end) ? start : this;
	}
}
