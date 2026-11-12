package dev.latvian.mods.klib.gradient;

import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.color.PositionedColor;
import dev.latvian.mods.klib.interpolation.Interpolation;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public record FlippedGradient(Ref<Gradient> gradient) implements Gradient {
	public static final DynamicType<ByteBuf, Gradient> TYPE = DynamicType.create(
		"flipped",
		"gradient",
		Gradient.CODEC,
		Gradient.STREAM_CODEC,
		FlippedGradient::new,
		FlippedGradient::gradient
	);

	@Override
	public DynamicType<ByteBuf, Gradient> type() {
		return TYPE;
	}

	@Override
	public Color get(float delta) {
		return gradient.value().get(1F - delta);
	}

	@Override
	public List<PositionedColor> getPositionedColors() {
		var gradientList = gradient.value().getPositionedColors();
		var list = new ArrayList<PositionedColor>(gradientList.size());

		for (int i = gradientList.size() - 1; i >= 0; i--) {
			var g = gradientList.get(i);
			list.add(new PositionedColor(1F - g.position(), g.color(), Interpolation.reverse(g.ease())));
		}

		return list;
	}
}
