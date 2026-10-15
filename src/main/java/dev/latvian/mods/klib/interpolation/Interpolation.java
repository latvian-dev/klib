package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.codec.KLibCodecErrors;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.math.Vec3f;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.EasingType;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface Interpolation extends CustomRegistryValue<ByteBuf, Interpolation> {
	CustomRegistry<ByteBuf, Interpolation> REGISTRY = CustomRegistry.<ByteBuf, Interpolation>builder("interpolation")
		.defaultType(LinearInterpolation.TYPE)
		.customCodec(directCodec -> KLibCodecs.or(List.of(
			Codec.FLOAT.<Interpolation>flatComapMap(FixedInterpolation::of, val -> {
				if (val instanceof FixedInterpolation fixed) {
					return DataResult.success(fixed.value());
				} else {
					return KLibCodecErrors.NOT_A_FLOAT;
				}
			}),
			directCodec
		)))
		.build();

	Codec<Ref<Interpolation>> CODEC = REGISTRY.codec();
	StreamCodec<ByteBuf, Ref<Interpolation>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<Interpolation>> DATA_TYPE = REGISTRY.dataType();

	static Ref<Interpolation> linear() {
		return LinearInterpolation.TYPE.unit();
	}

	static void builtInTypes(CustomRegistryTypeCollector<ByteBuf, Interpolation> registry) {
		registry.register(FixedInterpolation.TYPE);
		registry.register(ScaledInterpolation.TYPE);
		registry.register(CompositeInterpolation.TYPE);
		registry.register(JoinedInterpolation.TYPE);
		registry.register(InverseInterpolation.TYPE);
		registry.register(FlipXInterpolation.TYPE);
		registry.register(FlipYInterpolation.TYPE);
		registry.register(BezierInterpolation.TYPE);

		for (var type : EaseIn.VALUES) {
			registry.register(type.type);
		}

		for (var type : EaseOut.VALUES) {
			registry.register(type.type);
		}

		for (var type : CompositeInterpolation.EASING) {
			registry.register(type);
		}
	}

	@Override
	default CustomRegistry<ByteBuf, Interpolation> getRegistry() {
		return REGISTRY;
	}

	float interpolate(float x);

	default float interpolateClamped(float x) {
		return interpolate(Math.clamp(x, 0F, 1F));
	}

	default float interpolate(float x, float a, float b) {
		return KMath.lerp(interpolate(x), a, b);
	}

	default Vec3 interpolate(float x, Vec3 a, Vec3 b) {
		return KMath.lerp(interpolate(x), a, b);
	}

	default Vec3f interpolate(float x, Vec3f a, Vec3f b) {
		return a.lerp(interpolate(x), b);
	}

	default double interpolateMirrored(float x, Interpolation end) {
		return x < 0.5F ? interpolate(x * 2F) : 1F - end.interpolate((x - 0.5F) * 2F);
	}

	default double interpolateMirrored(float x) {
		return interpolateMirrored(x, this);
	}

	default boolean isLinear() {
		return false;
	}

	default EasingType toEasingType() {
		return new WrappedEasingType(ref());
	}
}
