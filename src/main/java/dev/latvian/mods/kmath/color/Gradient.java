package dev.latvian.mods.kmath.color;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kmath.codec.KMathStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public interface Gradient {
	Codec<Gradient> CODEC = Codec.lazyInitialized(() -> Codec.either(
		Codec.either(Color.CODEC, GradientReference.CODEC).xmap(e -> e.map(Function.identity(), Function.identity()), g -> g instanceof GradientReference r ? Either.right(r) : Either.left((Color) g)),
		Codec.either(CompoundGradient.CODEC, PairGradient.CODEC).xmap(e -> e.map(Function.identity(), Function.identity()), g -> g instanceof PairGradient r ? Either.right(r) : Either.left((CompoundGradient) g))
	).xmap(e -> e.map(Function.identity(), Function.identity()), g -> switch (g) {
		case Color v -> Either.left(v);
		case GradientReference v -> Either.left(v);
		case CompoundGradient v -> Either.right(v);
		case PairGradient v -> Either.right(v);
		case null, default -> Either.left(Color.TRANSPARENT);
	}));

	StreamCodec<ByteBuf, Gradient> STREAM_CODEC = KMathStreamCodecs.lazy(() -> ByteBufCodecs.either(
		ByteBufCodecs.either(Color.STREAM_CODEC, GradientReference.STREAM_CODEC).map(e -> e.map(Function.identity(), Function.identity()), g -> g instanceof GradientReference r ? Either.right(r) : Either.left((Color) g)),
		ByteBufCodecs.either(CompoundGradient.STREAM_CODEC, PairGradient.STREAM_CODEC).map(e -> e.map(Function.identity(), Function.identity()), g -> g instanceof PairGradient r ? Either.right(r) : Either.left((CompoundGradient) g))
	).map(e -> e.map(Function.identity(), Function.identity()), g -> switch (g) {
		case Color v -> Either.left(v);
		case GradientReference v -> Either.left(v);
		case CompoundGradient v -> Either.right(v);
		case PairGradient v -> Either.right(v);
		case null, default -> Either.left(Color.TRANSPARENT);
	}));

	Color get(float delta);

	default Color sample(RandomSource random) {
		return get(random.nextFloat());
	}

	default Gradient resolve() {
		return this;
	}
}
