package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.math.Vec3f;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.util.ID;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface Interpolation {
	CustomRegistry<ByteBuf, Interpolation> REGISTRY = CustomRegistry.<ByteBuf, Interpolation>builder()
		.keys(ID.klib("interpolation"), KLib.ID)
		.type(Interpolation::type)
		.server()
		.build();

	Codec<Interpolation> CODEC = REGISTRY.codec();
	StreamCodec<ByteBuf, Interpolation> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Interpolation> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC);

	static void builtInTypes(CustomRegistryTypeCollector<ByteBuf, Interpolation> registry) {
		registry.register(LinearInterpolation.TYPE);
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

	@Nullable
	default CustomRegistryType<ByteBuf, Interpolation> type() {
		return null;
	}

	double interpolate(double t);

	default float interpolate(float t) {
		return (float) interpolate((double) t);
	}

	default double interpolateClamped(double t) {
		return interpolate(Math.clamp(t, 0D, 1D));
	}

	default float interpolateClamped(float t) {
		return interpolate(Math.clamp(t, 0F, 1F));
	}

	default double interpolate(double t, double a, double b) {
		return KMath.lerp(interpolate(t), a, b);
	}

	default float interpolate(float t, float a, float b) {
		return KMath.lerp(interpolate(t), a, b);
	}

	default Vec3 interpolate(double t, Vec3 a, Vec3 b) {
		var e = interpolate(t);
		return new Vec3(KMath.lerp(e, a.x, b.x), KMath.lerp(e, a.y, b.y), KMath.lerp(e, a.z, b.z));
	}

	default Vec3f interpolate(float t, Vec3f a, Vec3f b) {
		var e = interpolate(t);
		return Vec3f.of(KMath.lerp(e, a.x(), b.x()), KMath.lerp(e, a.y(), b.y()), KMath.lerp(e, a.z(), b.z()));
	}

	default double interpolateMirrored(double x, Interpolation end) {
		return x < 0.5D ? interpolate(x * 2D) : 1D - end.interpolate((x - 0.5D) * 2D);
	}

	default double interpolateMirrored(double x) {
		return interpolateMirrored(x, this);
	}

	default Interpolation inverse() {
		return new InverseInterpolation(this);
	}

	default Interpolation flipX() {
		return new FlipXInterpolation(this);
	}

	default Interpolation flipY() {
		return new FlipYInterpolation(this);
	}

	default boolean isLinear() {
		return false;
	}

	default Interpolation composite(Interpolation other) {
		return CompositeInterpolation.of(this, other);
	}

	default Interpolation join(Interpolation other) {
		return new JoinedInterpolation(this, other);
	}
}
