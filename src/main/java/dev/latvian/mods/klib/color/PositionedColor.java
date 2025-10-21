package dev.latvian.mods.klib.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.interpolation.Interpolation;
import dev.latvian.mods.klib.interpolation.LinearInterpolation;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record PositionedColor(float position, Color color, Interpolation interpolation) implements Comparable<PositionedColor> {
	public static final PositionedColor[] EMPTY_ARRAY = new PositionedColor[0];
	public static final PositionedColor INVALID = new PositionedColor(Float.NaN, Color.TRANSPARENT, LinearInterpolation.INSTANCE);

	public static final Codec<PositionedColor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("position").forGetter(PositionedColor::position),
		Color.CODEC.fieldOf("colors").forGetter(PositionedColor::color),
		Interpolation.CODEC.optionalFieldOf("interpolation", LinearInterpolation.INSTANCE).forGetter(PositionedColor::interpolation)
	).apply(instance, PositionedColor::new));

	public static final StreamCodec<ByteBuf, PositionedColor> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, PositionedColor::position,
		Color.STREAM_CODEC, PositionedColor::color,
		Interpolation.STREAM_CODEC, PositionedColor::interpolation,
		PositionedColor::new
	);

	public PositionedColor(float position, Color color) {
		this(position, color, LinearInterpolation.INSTANCE);
	}

	@Override
	public int compareTo(@NotNull PositionedColor other) {
		return Float.compare(position, other.position);
	}

	public Color interpolate(float delta, PositionedColor other) {
		return color.lerp(interpolation.interpolate(delta), other.color);
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof PositionedColor c && Math.abs(position - c.position) <= 0.001F && color.argb() == c.color.argb() && interpolation == c.interpolation;
	}
}
