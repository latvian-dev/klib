package dev.latvian.mods.klib.math;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.SampledFloat;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record Range(float min, float max) implements SampledFloat {
	public static final Range ZERO = new Range(0F, 0F);
	public static final Range ONE = new Range(1F, 1F);
	public static final Range FULL = new Range(0F, 1F);

	public static final Codec<Range> CODEC = Codec.either(Codec.FLOAT, Codec.FLOAT.listOf(2, 2)).xmap(either -> either.map(Range::of, list -> of(list.getFirst(), list.getLast())), range -> range.isSame() ? Either.left(range.min) : Either.right(List.of(range.min, range.max)));

	public static final StreamCodec<ByteBuf, Range> STREAM_CODEC = CompositeStreamCodec.of(
		ByteBufCodecs.FLOAT, Range::min,
		ByteBufCodecs.FLOAT, Range::max,
		Range::new
	);

	public static final DataType<Range> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, Range.class);

	public Range(float min, float max) {
		this.min = Math.min(min, max);
		this.max = Math.max(min, max);
	}

	public static Range of(float min, float max) {
		return min == 0F && max == 0F ? ZERO : min == 1F && max == 1F ? ONE : Math.abs(max - min) < 0.0001F ? new Range(min, min) : new Range(Math.min(min, max), Math.max(min, max));
	}

	public static Range of(float value) {
		return of(value, value);
	}

	@Nullable
	public static Range ofTag(Tag nbt) {
		var fp = FloatPair.ofTag(nbt);
		return fp == null ? null : of(fp.a(), fp.b());
	}

	public Tag toTag() {
		return FloatPair.toTag(min, max);
	}

	public float get(float delta) {
		return KMath.lerp(delta, min, max);
	}

	@Override
	public float sample(RandomSource random) {
		return min == max ? min : KMath.lerp(random.nextFloat(), min, max);
	}

	public float delta(float value) {
		return (value - min) / (max - min);
	}

	public float average() {
		return (min + max) / 2F;
	}

	@Override
	public String toString() {
		return min == max ? KMath.format(min) : "[" + (KMath.format(min) + "," + KMath.format(max)) + "]";
	}

	public float clamp(float value) {
		return Math.clamp(value, min, max);
	}

	public boolean isZero() {
		return min == 0F && max == 0F;
	}

	public boolean isSame() {
		return min == max;
	}
}
