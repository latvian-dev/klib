package dev.latvian.mods.klib.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.interpolation.Interpolation;
import dev.latvian.mods.klib.registry.Ref;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record PositionedColor(float position, Color color, Ref<Interpolation> interpolation) implements Comparable<PositionedColor> {
	public static final PositionedColor[] EMPTY_ARRAY = new PositionedColor[0];
	public static final PositionedColor INVALID = new PositionedColor(Float.NaN, Color.TRANSPARENT, Interpolation.linear());

	public static final Codec<PositionedColor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("position").forGetter(PositionedColor::position),
		Color.CODEC.fieldOf("colors").forGetter(PositionedColor::color),
		Interpolation.CODEC.optionalFieldOf("interpolation", Interpolation.linear()).forGetter(PositionedColor::interpolation)
	).apply(instance, PositionedColor::new));

	public static final StreamCodec<ByteBuf, PositionedColor> STREAM_CODEC = CompositeStreamCodec.of(
		ByteBufCodecs.FLOAT, PositionedColor::position,
		Color.STREAM_CODEC, PositionedColor::color,
		Interpolation.STREAM_CODEC, PositionedColor::interpolation,
		PositionedColor::new
	);

	public static final StreamCodec<ByteBuf, List<PositionedColor>> LIST_STREAM_CODEC = new StreamCodec<>() {
		@Override
		public List<PositionedColor> decode(ByteBuf buf) {
			int size = VarInt.read(buf);

			if (size == 0) {
				return List.of();
			} else if (size == 1) {
				return List.of(new PositionedColor(0F, Color.STREAM_CODEC.decode(buf)));
			}

			var list = new ArrayList<PositionedColor>(size);

			if (buf.readBoolean()) {
				for (int i = 0; i < size; i++) {
					var position = i / (float) (size - 1);
					list.add(new PositionedColor(position, Color.STREAM_CODEC.decode(buf)));
				}
			} else {
				for (int i = 0; i < size; i++) {
					list.add(STREAM_CODEC.decode(buf));
				}
			}

			return list;
		}

		@Override
		public void encode(ByteBuf buf, List<PositionedColor> value) {
			VarInt.write(buf, value.size());

			if (value.isEmpty()) {
				return;
			} else if (value.size() == 1) {
				Color.STREAM_CODEC.encode(buf, value.getFirst().color());
				return;
			}

			if (isSimple(value)) {
				buf.writeBoolean(true);

				for (var c : value) {
					Color.STREAM_CODEC.encode(buf, c.color);
				}
			} else {
				buf.writeBoolean(false);

				for (var c : value) {
					STREAM_CODEC.encode(buf, c);
				}
			}
		}
	};

	public static List<PositionedColor> fromSimpleList(List<Color> colors) {
		if (colors.isEmpty()) {
			return List.of();
		} else if (colors.size() == 1) {
			return List.of(new PositionedColor(0F, colors.getFirst()));
		}

		var list = new ArrayList<PositionedColor>(colors.size());

		for (int i = 0; i < colors.size(); i++) {
			var position = i / (float) (colors.size() - 1);
			list.add(new PositionedColor(position, colors.get(i)));
		}

		return list;
	}

	public static List<Color> toSimpleList(List<PositionedColor> colors) {
		if (colors.isEmpty()) {
			return List.of();
		} else if (colors.size() == 1) {
			return List.of(colors.getFirst().color);
		}

		var list = new ArrayList<Color>(colors.size());

		for (var c : colors) {
			list.add(c.color);
		}

		return list;
	}

	private static final Codec<List<PositionedColor>> LIST_CODEC_OF_SIMPLE_COLORS = Color.CODEC.listOf().flatComapMap(PositionedColor::fromSimpleList, colors -> {
		if (isSimple(colors)) {
			return DataResult.success(toSimpleList(colors));
		} else {
			return DataResult.error(() -> "Not a simple list");
		}
	});

	public static boolean isSimple(List<PositionedColor> colors) {
		if (colors.size() <= 1) {
			return true;
		}

		int size = colors.size();

		for (int i = 0; i < size; i++) {
			var c = colors.get(i);
			var position = i / (float) (size - 1);

			if (!c.interpolation().value().isLinear() || Math.abs(c.position() - position) > 0.001F) {
				return false;
			}
		}

		return true;
	}

	public static final Codec<List<PositionedColor>> LIST_CODEC = KLibCodecs.or(LIST_CODEC_OF_SIMPLE_COLORS, CODEC.listOf());

	public PositionedColor(float position, Color color) {
		this(position, color, Interpolation.linear());
	}

	@Override
	public int compareTo(@NotNull PositionedColor other) {
		return Float.compare(position, other.position);
	}

	public Color interpolate(float delta, PositionedColor other) {
		return color.lerp(interpolation.value().interpolate(delta), other.color);
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof PositionedColor c && Math.abs(position - c.position) <= 0.001F && color.argb() == c.color.argb() && interpolation == c.interpolation;
	}
}
