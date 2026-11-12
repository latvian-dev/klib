package dev.latvian.mods.klib.gradient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.codec.KLibCodecErrors;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.color.PositionedColor;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

import java.util.List;

public interface Gradient extends CustomRegistryValue<ByteBuf, Gradient> {
	UnitType<ByteBuf, Gradient> EMPTY = UnitType.create("empty", FlatColorGradient.EMPTY);
	UnitType<ByteBuf, Gradient> WHITE = UnitType.create("white", FlatColorGradient.WHITE);
	UnitType<ByteBuf, Gradient> BLACK = UnitType.create("black", FlatColorGradient.BLACK);
	UnitType<ByteBuf, Gradient> RED = UnitType.create("red", FlatColorGradient.RED);
	UnitType<ByteBuf, Gradient> GREEN = UnitType.create("green", FlatColorGradient.GREEN);
	UnitType<ByteBuf, Gradient> BLUE = UnitType.create("blue", FlatColorGradient.BLUE);
	UnitType<ByteBuf, Gradient> YELLOW = UnitType.create("yellow", FlatColorGradient.YELLOW);
	UnitType<ByteBuf, Gradient> MAGENTA = UnitType.create("magenta", FlatColorGradient.MAGENTA);
	UnitType<ByteBuf, Gradient> CYAN = UnitType.create("cyan", FlatColorGradient.CYAN);

	UnitType<ByteBuf, Gradient> FADE_OUT_WHITE = UnitType.create("fade_out_white", new LinearGradient(Color.WHITE, Color.WHITE.withAlpha(0)));
	UnitType<ByteBuf, Gradient> FADE_OUT_BLACK = UnitType.create("fade_out_black", new LinearGradient(Color.BLACK, Color.BLACK.withAlpha(0)));
	UnitType<ByteBuf, Gradient> FADE_IN_WHITE = UnitType.create("fade_in_white", new LinearGradient(Color.WHITE.withAlpha(0), Color.WHITE));
	UnitType<ByteBuf, Gradient> FADE_IN_BLACK = UnitType.create("fade_in_black", new LinearGradient(Color.BLACK.withAlpha(0), Color.BLACK));

	CustomRegistry<ByteBuf, Gradient> REGISTRY = CustomRegistry.<ByteBuf, Gradient>builder("gradient")
		.defaultType(EMPTY)
		.customCodec(directCodec -> KLibCodecs.or(List.of(
			Color.CODEC.<Gradient>flatComapMap(FlatColorGradient::of, gradient -> {
				if (gradient instanceof FlatColorGradient f) {
					return DataResult.success(f.color());
				} else {
					return KLibCodecErrors.NOT_A_COLOR;
				}
			}),
			directCodec
		)))
		.build();

	Codec<Ref<Gradient>> CODEC = REGISTRY.codec();
	StreamCodec<ByteBuf, Ref<Gradient>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<Gradient>> DATA_TYPE = REGISTRY.dataType();

	static void builtInTypes(CustomRegistryTypeCollector<ByteBuf, Gradient> registry) {
		registry.register(WHITE);
		registry.register(BLACK);
		registry.register(RED);
		registry.register(GREEN);
		registry.register(BLUE);
		registry.register(YELLOW);
		registry.register(MAGENTA);
		registry.register(CYAN);

		registry.register(FADE_OUT_WHITE);
		registry.register(FADE_OUT_BLACK);

		registry.register(FlatColorGradient.TYPE);
		registry.register(LinearGradient.TYPE);
		registry.register(CompoundGradient.TYPE);
		registry.register(FlippedGradient.TYPE);
	}

	@Override
	default CustomRegistry<ByteBuf, Gradient> getRegistry() {
		return REGISTRY;
	}

	Color get(float delta);

	default Color sample(RandomSource random) {
		return get(random.nextFloat());
	}

	default List<PositionedColor> getPositionedColors() {
		return List.of();
	}
}
