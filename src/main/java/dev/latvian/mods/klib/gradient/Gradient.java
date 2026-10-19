package dev.latvian.mods.klib.gradient;

import com.mojang.serialization.Codec;
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

	CustomRegistry<ByteBuf, Gradient> REGISTRY = CustomRegistry.create("gradient", EMPTY);
	Codec<Ref<Gradient>> CODEC = REGISTRY.codec();
	StreamCodec<ByteBuf, Ref<Gradient>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<Gradient>> DATA_TYPE = REGISTRY.dataType();

	CustomRegistry<ByteBuf, Gradient> CLIENT_REGISTRY = CustomRegistry.createNoValueSync("client_gradient", EMPTY);

	static void builtInTypes(CustomRegistryTypeCollector<ByteBuf, Gradient> registry) {
		registry.register(WHITE);
		registry.register(BLACK);
		registry.register(RED);
		registry.register(GREEN);
		registry.register(BLUE);
		registry.register(YELLOW);
		registry.register(MAGENTA);
		registry.register(CYAN);
		registry.register(FlatColorGradient.TYPE);
		registry.register(LinearGradient.TYPE);
		registry.register(CompoundGradient.TYPE);
		registry.register(ClientGradient.TYPE);
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
