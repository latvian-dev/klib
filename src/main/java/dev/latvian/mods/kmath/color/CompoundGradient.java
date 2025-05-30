package dev.latvian.mods.kmath.color;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kmath.KMath;
import dev.latvian.mods.kmath.easing.Easing;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.function.Function;

public record CompoundGradient(List<Gradient> children, Easing easing) implements Gradient {
	public static final Codec<CompoundGradient> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Gradient.CODEC.listOf().fieldOf("children").forGetter(CompoundGradient::children),
		Easing.CODEC.optionalFieldOf("easing", Easing.LINEAR).forGetter(CompoundGradient::easing)
	).apply(instance, CompoundGradient::new));

	public static final Codec<CompoundGradient> CODEC = Codec.either(DIRECT_CODEC, Gradient.CODEC.listOf()).xmap(e -> e.map(Function.identity(), CompoundGradient::new), g -> g.easing == Easing.LINEAR ? Either.right(g.children) : Either.left(g));

	public static final StreamCodec<ByteBuf, CompoundGradient> STREAM_CODEC = StreamCodec.composite(
		Gradient.STREAM_CODEC.apply(ByteBufCodecs.list()), CompoundGradient::children,
		Easing.STREAM_CODEC, CompoundGradient::easing,
		CompoundGradient::new
	);

	public CompoundGradient(List<? extends Gradient> children) {
		this((List) children, Easing.LINEAR);
	}

	@Override
	public Color get(float delta) {
		delta = easing.ease(delta);

		if (delta <= 0F) {
			return children.getFirst().get(0F);
		} else if (delta >= 1F) {
			return children.getLast().get(1F);
		} else if (children.size() == 1) {
			return children.getFirst().get(delta);
		}

		float indexf = delta * (children.size() - 1F);

		int si = (int) indexf;
		int ei = si + 1;

		var s = children.get(si);
		var e = children.get(ei);

		var d = KMath.map(indexf, si, ei, 0F, 1F);

		if (d < 0.5F) {
			return s.get(d * 2F).lerp(d, e.get(0F));
		} else {
			return s.get(1F).lerp(d, e.get((d - 0.5F) * 2F));
		}
	}

	@Override
	public Gradient resolve() {
		if (children.isEmpty()) {
			return Color.TRANSPARENT;
		} else if (children.size() == 2) {
			return new PairGradient(children.getFirst(), children.getLast(), easing).resolve();
		} else if (children.size() == 1 && easing == Easing.LINEAR) {
			return children.getFirst().resolve();
		} else {
			return this;
		}
	}
}
