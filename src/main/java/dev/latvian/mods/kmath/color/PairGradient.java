package dev.latvian.mods.kmath.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kmath.codec.KMathStreamCodecs;
import dev.latvian.mods.kmath.easing.Easing;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PairGradient(Gradient start, Gradient end, Easing easing) implements Gradient {
	public static final Codec<PairGradient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Gradient.CODEC.fieldOf("start").forGetter(PairGradient::start),
		Gradient.CODEC.fieldOf("end").forGetter(PairGradient::end),
		Easing.CODEC.optionalFieldOf("easing", Easing.LINEAR).forGetter(PairGradient::easing)
	).apply(instance, PairGradient::new));

	public static final StreamCodec<ByteBuf, PairGradient> STREAM_CODEC = StreamCodec.composite(
		Gradient.STREAM_CODEC, PairGradient::start,
		Gradient.STREAM_CODEC, PairGradient::end,
		KMathStreamCodecs.optional(Easing.STREAM_CODEC, Easing.LINEAR), PairGradient::easing,
		PairGradient::new
	);

	public PairGradient(Gradient start, Gradient end) {
		this(start, end, Easing.LINEAR);
	}

	@Override
	public Color get(float delta) {
		float d = easing.ease(delta);

		if (d < 0.5F) {
			return start.get(d * 2F).lerp(d, end.get(0F));
		} else {
			return start.get(1F).lerp(d, end.get((d - 0.5F) * 2F));
		}
	}

	@Override
	public Gradient resolve() {
		return start.equals(end) ? start : this;
	}
}
