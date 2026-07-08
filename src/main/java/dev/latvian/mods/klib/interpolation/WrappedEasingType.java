package dev.latvian.mods.klib.interpolation;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.util.EasingType;

import java.util.function.Function;

public record WrappedEasingType(Ref<Interpolation> ref) implements EasingType {
	public static Codec<EasingType> wrap(Codec<EasingType> original) {
		return Codec.either(original, Interpolation.CODEC).xmap(either -> either.map(Function.identity(), WrappedEasingType::new), easingType -> {
			if (easingType instanceof WrappedEasingType t) {
				return Either.right(t.ref());
			} else {
				return Either.left(easingType);
			}
		});
	}

	@Override
	public float apply(float x) {
		return ref.value().interpolate(x);
	}
}
