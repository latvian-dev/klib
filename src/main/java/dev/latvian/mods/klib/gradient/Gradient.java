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
	UnitType<ByteBuf, Gradient> EMPTY = UnitType.create("empty", new FlatColorGradient(Color.TRANSPARENT));
	UnitType<ByteBuf, Gradient> WHITE = UnitType.create("white", new FlatColorGradient(Color.WHITE));
	UnitType<ByteBuf, Gradient> BLACK = UnitType.create("black", new FlatColorGradient(Color.BLACK));
	UnitType<ByteBuf, Gradient> RED = UnitType.create("red", new FlatColorGradient(Color.RED));
	UnitType<ByteBuf, Gradient> GREEN = UnitType.create("green", new FlatColorGradient(Color.GREEN));
	UnitType<ByteBuf, Gradient> BLUE = UnitType.create("blue", new FlatColorGradient(Color.BLUE));
	UnitType<ByteBuf, Gradient> YELLOW = UnitType.create("yellow", new FlatColorGradient(Color.YELLOW));
	UnitType<ByteBuf, Gradient> MAGENTA = UnitType.create("magenta", new FlatColorGradient(Color.MAGENTA));
	UnitType<ByteBuf, Gradient> CYAN = UnitType.create("cyan", new FlatColorGradient(Color.CYAN));

	CustomRegistry<ByteBuf, Gradient> REGISTRY = CustomRegistry.create("gradient", EMPTY);
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
