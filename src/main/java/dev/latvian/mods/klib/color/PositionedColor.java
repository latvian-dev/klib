package dev.latvian.mods.klib.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.easing.Easing;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record PositionedColor(float position, Color color, Easing easing) implements Comparable<PositionedColor> {
	public static final PositionedColor[] EMPTY_ARRAY = new PositionedColor[0];
	public static final PositionedColor INVALID = new PositionedColor(Float.NaN, Color.TRANSPARENT, Easing.LINEAR);

	public static final Codec<PositionedColor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("position").forGetter(PositionedColor::position),
		Color.CODEC.fieldOf("colors").forGetter(PositionedColor::color),
		Easing.CODEC.optionalFieldOf("easing", Easing.LINEAR).forGetter(PositionedColor::easing)
	).apply(instance, PositionedColor::new));

	public static final StreamCodec<ByteBuf, PositionedColor> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, PositionedColor::position,
		Color.STREAM_CODEC, PositionedColor::color,
		Easing.STREAM_CODEC, PositionedColor::easing,
		PositionedColor::new
	);

	@Override
	public int compareTo(@NotNull PositionedColor other) {
		return Float.compare(position, other.position);
	}

	public Color interpolate(float delta, PositionedColor other) {
		return color.lerp(easing.ease(delta), other.color);
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof PositionedColor c && Math.abs(position - c.position) <= 0.001F && color.argb() == c.color.argb() && easing == c.easing;
	}
}
