package dev.latvian.mods.klib.color;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.easing.Easing;
import dev.latvian.mods.klib.math.KMath;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public final class CompoundGradient implements Gradient {
	public static final Codec<CompoundGradient> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		PositionedColor.CODEC.listOf().fieldOf("colors").forGetter(c -> c.sortedList)
	).apply(instance, CompoundGradient::new));

	public static CompoundGradient ofColors(List<Color> colors) {
		var list = new ArrayList<PositionedColor>(colors.size());

		for (int i = 0; i < colors.size(); i++) {
			list.add(new PositionedColor(i / (float) (colors.size() - 1), colors.get(i), Easing.LINEAR));
		}

		return new CompoundGradient(list);
	}

	public static final Codec<CompoundGradient> CODEC = Codec.either(DIRECT_CODEC, Color.CODEC.listOf()).xmap(e -> e.map(Function.identity(), CompoundGradient::ofColors), g -> g.isSimple() ? Either.right(g.getRawColors()) : Either.left(g));
	public static final StreamCodec<ByteBuf, CompoundGradient> STREAM_CODEC = PositionedColor.STREAM_CODEC.apply(ByteBufCodecs.list()).map(CompoundGradient::new, c -> c.sortedList);

	private final PositionedColor[] sorted;
	private final List<PositionedColor> sortedList;

	private CompoundGradient(PositionedColor[] sorted) {
		this.sorted = sorted;
		Arrays.sort(this.sorted);
		this.sortedList = Arrays.asList(this.sorted);
	}

	public CompoundGradient(List<PositionedColor> colors) {
		this(colors.toArray(PositionedColor.EMPTY_ARRAY));
	}

	@Override
	public Color get(float delta) {
		if (sorted.length == 0) {
			return Color.TRANSPARENT;
		} else if (delta <= 0F || sorted.length == 1) {
			return sorted[0].color();
		} else if (delta >= 1F) {
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
	public Gradient resolve() {
		if (sorted.length == 0) {
			return Color.TRANSPARENT;
		} else if (sorted.length == 2 && sorted[0].easing() == Easing.LINEAR) {
			return new LinearPairGradient(sorted[0].color(), sorted[sorted.length - 1].color()).resolve();
		} else if (sorted.length == 1) {
			return sorted[0].color().resolve();
		} else {
			return this;
		}
	}

	public boolean isSimple() {
		for (int i = 0; i < sorted.length; i++) {
			var c = sorted[i];

			if (c.easing() != Easing.LINEAR || Math.abs(c.position() - (i / (float) (sorted.length - 1))) > 0.001F) {
				return false;
			}
		}

		return true;
	}

	public List<PositionedColor> getColors() {
		return List.copyOf(sortedList);
	}

	public List<Color> getRawColors() {
		var list = new ArrayList<Color>(sorted.length);

		for (var c : sorted) {
			list.add(c.color());
		}

		return list;
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
