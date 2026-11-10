package dev.latvian.mods.klib.gradient;

import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.color.PositionedColor;
import dev.latvian.mods.klib.registry.DynamicType;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record FlatColorGradient(Color color) implements Gradient {
	public static final FlatColorGradient EMPTY = new FlatColorGradient(Color.TRANSPARENT);
	public static final FlatColorGradient WHITE = new FlatColorGradient(Color.WHITE);
	public static final FlatColorGradient BLACK = new FlatColorGradient(Color.BLACK);
	public static final FlatColorGradient RED = new FlatColorGradient(Color.RED);
	public static final FlatColorGradient GREEN = new FlatColorGradient(Color.GREEN);
	public static final FlatColorGradient BLUE = new FlatColorGradient(Color.BLUE);
	public static final FlatColorGradient YELLOW = new FlatColorGradient(Color.YELLOW);
	public static final FlatColorGradient MAGENTA = new FlatColorGradient(Color.MAGENTA);
	public static final FlatColorGradient CYAN = new FlatColorGradient(Color.CYAN);

	public static FlatColorGradient of(Color color) {
		if (color == Color.TRANSPARENT) {
			return EMPTY;
		} else if (color == Color.WHITE) {
			return WHITE;
		} else if (color == Color.BLACK) {
			return BLACK;
		} else if (color == Color.RED) {
			return RED;
		} else if (color == Color.GREEN) {
			return GREEN;
		} else if (color == Color.BLUE) {
			return BLUE;
		} else if (color == Color.YELLOW) {
			return YELLOW;
		} else if (color == Color.MAGENTA) {
			return MAGENTA;
		} else if (color == Color.CYAN) {
			return CYAN;
		} else {
			return new FlatColorGradient(color);
		}
	}

	public static final DynamicType<ByteBuf, Gradient> TYPE = DynamicType.create(
		"color",
		"color",
		Color.CODEC,
		Color.STREAM_CODEC,
		FlatColorGradient::of,
		FlatColorGradient::color
	);

	@Override
	public DynamicType<ByteBuf, Gradient> type() {
		return TYPE;
	}

	@Override
	public Color get(float delta) {
		return color;
	}

	@Override
	public Color sample(RandomSource random) {
		return color;
	}

	@Override
	public List<PositionedColor> getPositionedColors() {
		return List.of(new PositionedColor(0F, color));
	}

	@Override
	public @NotNull String toString() {
		return color.toString();
	}
}
