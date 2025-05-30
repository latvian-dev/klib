package dev.latvian.mods.kmath.color;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kmath.codec.KMathStreamCodecs;
import dev.latvian.mods.kmath.easing.Easing;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public record TwoColorGradient(Color start, Color end, Easing easing) implements Gradient {
	public static final Codec<TwoColorGradient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Color.CODEC.fieldOf("start").forGetter(TwoColorGradient::start),
		Color.CODEC.fieldOf("end").forGetter(TwoColorGradient::end),
		Easing.CODEC.optionalFieldOf("easing", Easing.LINEAR).forGetter(TwoColorGradient::easing)
	).apply(instance, TwoColorGradient::new));

	public static final StreamCodec<ByteBuf, TwoColorGradient> STREAM_CODEC = StreamCodec.composite(
		Color.STREAM_CODEC, TwoColorGradient::start,
		Color.STREAM_CODEC, TwoColorGradient::end,
		KMathStreamCodecs.optional(Easing.STREAM_CODEC, Easing.LINEAR), TwoColorGradient::easing,
		TwoColorGradient::new
	);

	public static final Codec<TwoColorGradient> OPTIONAL_CODEC = Codec.either(CODEC, Color.CODEC).xmap(e -> e.map(Function.identity(), c -> new TwoColorGradient(c, c, Easing.LINEAR)), g -> g.start.equals(g.end) && g.easing == Easing.LINEAR ? Either.right(g.start) : Either.left(g));
	public static final StreamCodec<ByteBuf, TwoColorGradient> OPTIONAL_STREAM_CODEC = ByteBufCodecs.either(STREAM_CODEC, Color.STREAM_CODEC).map(e -> e.map(Function.identity(), c -> new TwoColorGradient(c, c, Easing.LINEAR)), g -> g.start.equals(g.end) && g.easing == Easing.LINEAR ? Either.right(g.start) : Either.left(g));

	public TwoColorGradient(Color start, Color end) {
		this(start, end, Easing.LINEAR);
	}

	@Override
	public Color get(float delta) {
		return start.lerp(easing.ease(delta), end);
	}
}
