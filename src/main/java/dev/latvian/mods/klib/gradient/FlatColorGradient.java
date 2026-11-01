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

	public static final DynamicType<ByteBuf, Gradient> TYPE = DynamicType.create(
		"color",
		"color",
		Color.CODEC,
		Color.STREAM_CODEC,
		FlatColorGradient::new,
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
