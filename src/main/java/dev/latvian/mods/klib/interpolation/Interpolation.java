package dev.latvian.mods.klib.interpolation;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.math.Vec3f;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public interface Interpolation {
	Codec<Interpolation> UNIT_CODEC = InterpolationType.CODEC.comapFlatMap(t -> t.unit() == null ? DataResult.error(() -> "Not a unit type") : DataResult.success(t.unit()), Interpolation::type);
	Codec<Interpolation> DIRECT_CODEC = InterpolationType.CODEC.dispatch("type", Interpolation::type, InterpolationType::mapCodec);
	Codec<Interpolation> CODEC = Codec.either(UNIT_CODEC, DIRECT_CODEC).xmap(Either::unwrap, i -> i.type().unit() == null ? Either.right(i) : Either.left(i));

	StreamCodec<ByteBuf, Interpolation> STREAM_CODEC = InterpolationType.STREAM_CODEC.dispatch(Interpolation::type, InterpolationType::streamCodec);

	DataType<Interpolation> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, Interpolation.class);

	InterpolationType<?> type();

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
