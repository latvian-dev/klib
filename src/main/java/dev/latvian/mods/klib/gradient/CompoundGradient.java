package dev.latvian.mods.klib.gradient;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.color.PositionedColor;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.registry.DynamicType;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.List;

public final class CompoundGradient implements Gradient {
	public static final DynamicType<ByteBuf, Gradient> TYPE = DynamicType.create(
		"compound",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			PositionedColor.LIST_CODEC.fieldOf("colors").forGetter(CompoundGradient::getPositionedColors)
		).apply(instance, CompoundGradient::new)),
		PositionedColor.LIST_STREAM_CODEC.map(CompoundGradient::new, CompoundGradient::getPositionedColors)
	);

	private final PositionedColor[] sorted;
	private final List<PositionedColor> sortedList;
	private final float leftMostPosition;
	private final float rightMostPosition;

	private CompoundGradient(PositionedColor[] sorted) {
		this.sorted = sorted;
		Arrays.sort(this.sorted);
		this.sortedList = List.of(this.sorted);

		float leftMostPosition = 1F;
		float rightMostPosition = 0F;

		for (var c : sorted) {
			if (c.position() < leftMostPosition) {
				leftMostPosition = c.position();
			}

			if (c.position() > rightMostPosition) {
				rightMostPosition = c.position();
			}
		}

		this.leftMostPosition = leftMostPosition;
		this.rightMostPosition = rightMostPosition;
	}

	public CompoundGradient(List<PositionedColor> colors) {
		this(colors.toArray(PositionedColor.EMPTY_ARRAY));
	}

	@Override
	public Color get(float delta) {
		if (sorted.length == 0) {
			return Color.TRANSPARENT;
		} else if (delta <= leftMostPosition || sorted.length == 1) {
			return sorted[0].color();
		} else if (delta >= rightMostPosition) {
			return sorted[sorted.length - 1].color();
		}

		var left = sorted[0];
		var right = sorted[sorted.length - 1];

		for (int i = sorted.length - 1; i >= 0; i--) {
			var c = sorted[i];

			if (c.position() <= delta) {
				left = c;
				right = i < sorted.length - 1 ? sorted[i + 1] : c;
				break;
			}
		}

		return left.interpolate(KMath.map(delta, left.position(), right.position(), 0F, 1F), right);
	}

	@Override
	public Gradient optimize() {
		if (sorted.length == 0) {
			return Gradient.EMPTY.value();
		} else if (sorted.length == 2 && sorted[0].interpolation().value().isLinear()) {
			return new LinearGradient(sorted[0].color(), sorted[sorted.length - 1].color()).optimize();
		} else if (sorted.length == 1) {
			return sorted[0].color().toGradient();
		} else {
			return this;
		}
	}

	@Override
	public List<PositionedColor> getPositionedColors() {
		return sortedList;
	}

	@Override
	public boolean equals(Object o) {
		return o == this || (o instanceof CompoundGradient c && sortedList.equals(c.sortedList));
	}

	@Override
	public int hashCode() {
		return sortedList.hashCode();
	}

	@Override
	public String toString() {
		return "CompoundGradient" + sortedList;
	}

}
