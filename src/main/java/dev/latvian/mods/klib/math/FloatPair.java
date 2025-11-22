package dev.latvian.mods.klib.math;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record FloatPair(float a, float b) {
	public static final FloatPair ZERO = new FloatPair(0F, 0F);
	public static final FloatPair ONE = new FloatPair(1F, 1F);

	public static final Codec<FloatPair> CODEC = Codec.either(Codec.FLOAT, Codec.FLOAT.listOf(2, 2)).xmap(either -> either.map(FloatPair::of, list -> of(list.getFirst(), list.getLast())), pair -> pair.a == pair.b ? Either.left(pair.a) : Either.right(List.of(pair.a, pair.b)));

	public static final StreamCodec<ByteBuf, FloatPair> STREAM_CODEC = CompositeStreamCodec.of(
		ByteBufCodecs.FLOAT, FloatPair::a,
		ByteBufCodecs.FLOAT, FloatPair::b,
		FloatPair::new
	);

	public static FloatPair of(float a, float b) {
		return a == 0F && b == 0F ? ZERO : a == 1F && b == 1F ? ONE : Math.abs(a - b) < 0.0001F ? new FloatPair(a, a) : new FloatPair(a, b);
	}

	public static FloatPair of(float value) {
		return of(value, value);
	}

	@Nullable
	public static FloatPair ofTag(@Nullable Tag nbt) {
		return switch (nbt) {
			case ListTag list -> of(list.getFloatOr(0, 0F), list.getFloatOr(1, 0F));
			case NumericTag num -> of(num.floatValue());
			case IntArrayTag arr -> of(arr.get(0).floatValue(), arr.get(1).floatValue());
			case ByteArrayTag arr -> of(arr.get(0).floatValue(), arr.get(1).floatValue());
			case null, default -> null;
		};
	}

	public static Tag toTag(float a, float b) {
		if (a == b) {
			return KMath.tag(a);
		}

		var mn = KMath.tag(a);
		var mx = KMath.tag(b);

		if (mn.getId() == mx.getId()) {
			if (mn.getId() == Tag.TAG_BYTE) {
				return new ByteArrayTag(new byte[]{(byte) a, (byte) b});
			} else if (mn.getId() == Tag.TAG_INT || mn.getId() == Tag.TAG_SHORT) {
				return new IntArrayTag(new int[]{(int) a, (int) b});
			} else {
				var list = new ListTag();
				list.add(mn);
				list.add(mx);
				return list;
			}
		} else {
			var list = new ListTag();
			list.add(FloatTag.valueOf(a));
			list.add(FloatTag.valueOf(b));
			return list;
		}
	}

	public Tag toTag() {
		return toTag(a, b);
	}

	@Override
	public String toString() {
		return a == b ? KMath.format(a) : (KMath.format(a) + " & " + KMath.format(b));
	}
}
