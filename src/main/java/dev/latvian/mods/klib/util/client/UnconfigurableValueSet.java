package dev.latvian.mods.klib.util.client;

import com.mojang.serialization.Codec;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public record UnconfigurableValueSet<T>(String captionId, Codec<T> codec) implements OptionInstance.ValueSet<T> {
	public static <T> OptionInstance<T> create(
		String caption,
		Codec<T> codec,
		OptionInstance.TooltipSupplier<T> tooltip,
		T initialValue,
		Consumer<T> onValueUpdate
	) {
		return new OptionInstance<>(
			caption,
			tooltip,
			(c, value) -> Component.empty(),
			new UnconfigurableValueSet<>(caption, codec),
			codec,
			initialValue,
			onValueUpdate::accept
		);
	}

	public static <T> OptionInstance<T> create(String caption, Codec<T> codec, T initialValue) {
		return create(caption, codec, OptionInstance.noTooltip(), initialValue, t -> {
		});
	}

	@Override
	public Function<OptionInstance<T>, AbstractWidget> createButton(OptionInstance.TooltipSupplier<T> tooltip, Options options, int x, int y, int width, OptionInstance.ValueUpdateListener<? super T> onChange) {
		return inst -> {
			// 26.3 no longer exposes OptionInstance#caption, so the caption id is kept alongside the codec.
			var button = Button.builder(Component.translatable(captionId), button1 -> {
				})
				.bounds(x, y, width, 20)
				.tooltip(tooltip.apply(inst.get()))
				.build();

			button.active = false;
			return button;
		};
	}

	@Override
	public Optional<T> validateValue(T value) {
		return Optional.of(value);
	}
}
