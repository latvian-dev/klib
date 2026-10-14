package dev.latvian.mods.klib.gradient;

import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.color.PositionedColor;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.RandomSource;

import java.util.List;

public record ClientGradient(Ref<Gradient> gradient) implements Gradient {
	public static final DynamicType<ByteBuf, Gradient> TYPE = DynamicType.create(
		"client",
		"gradient",
		Gradient.CLIENT_REGISTRY.codec(),
		Gradient.CLIENT_REGISTRY.streamCodec(),
		ClientGradient::new,
		ClientGradient::gradient
	);

	@Override
	public DynamicType<ByteBuf, Gradient> type() {
		return TYPE;
	}

	@Override
	public Color get(float delta) {
		return gradient.value().get(delta);
	}

	@Override
	public Color sample(RandomSource random) {
		return gradient.value().sample(random);
	}

	@Override
	public List<PositionedColor> getPositionedColors() {
		return gradient.value().getPositionedColors();
	}
}
