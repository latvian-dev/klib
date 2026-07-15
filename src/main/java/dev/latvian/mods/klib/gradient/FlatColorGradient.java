package dev.latvian.mods.klib.gradient;

import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.color.PositionedColor;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record FlatColorGradient(Color color) implements Gradient {
	public static final DynamicType<ByteBuf, Gradient> TYPE = DynamicType.create(
		"color",
		"color",
		Color.CODEC,
		Color.STREAM_CODEC,
		FlatColorGradient::new,
		FlatColorGradient::color
	);

	@Override
	public CustomRegistryType<ByteBuf, Gradient> type() {
		return color.argb() == 0 ? EMPTY : TYPE;
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
	public @NonNull String toString() {
		return color.toString();
	}
}
