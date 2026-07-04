package dev.latvian.mods.klib.gradient;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.color.PositionedColor;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.RefOptimizer;
import dev.latvian.mods.klib.util.ID;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Gradient extends RefOptimizer<Gradient> {
	CustomRegistry<ByteBuf, Gradient> REGISTRY = CustomRegistry.<ByteBuf, Gradient>builder()
		.keys(ID.klib("gradient"), "vidlib")
		.type(Gradient::type)
		.noValueSync()
		.build();

	CustomRegistryType.Unit<ByteBuf, Gradient> EMPTY = REGISTRY.unit(ID.klib("empty"), new FlatColorGradient(Color.TRANSPARENT));
	Codec<Ref<Gradient>> CODEC = REGISTRY.codec();
	StreamCodec<ByteBuf, Ref<Gradient>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<Gradient>> DATA_TYPE = REGISTRY.dataType();

	static void builtInTypes(CustomRegistryTypeCollector<ByteBuf, Gradient> registry) {
		registry.register(EMPTY);
		registry.register(FlatColorGradient.TYPE);
		registry.register(LinearGradient.TYPE);
		registry.register(CompoundGradient.TYPE);
		registry.register(ClientGradient.TYPE);
	}

	@Nullable
	default CustomRegistryType<ByteBuf, Gradient> type() {
		return null;
	}

	Color get(float delta);

	default Color sample(RandomSource random) {
		return get(random.nextFloat());
	}

	default List<PositionedColor> getPositionedColors() {
		return List.of();
	}
}
