package dev.latvian.mods.klib.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.codec.KLibCodecErrors;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Reference2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record BlockStatePalette(@Nullable UnitType<ByteBuf, BlockStatePalette> typeOverride, Reference2IntMap<BlockState> map, int totalWeight) implements CustomRegistryValue<ByteBuf, BlockStatePalette> {
	private static final DataResult<BlockState> NOT_SINGLE_BLOCK = KLibCodecErrors.error("Not a single block");

	public static final DynamicType<ByteBuf, BlockStatePalette> TYPE = DynamicType.create(
		"default",
		"palette",
		MCCodecs.STATE_TO_INT_MAP,
		MCStreamCodecs.STATE_TO_INT_MAP,
		BlockStatePalette::new,
		BlockStatePalette::map
	);

	public static class Builder {
		private final Reference2IntMap<BlockState> map = new Reference2IntLinkedOpenHashMap<>(1);

		public Builder add(BlockState state, int weight) {
			map.put(state, weight);
			return this;
		}

		public BlockStatePalette build() {
			return new BlockStatePalette(map);
		}

		public UnitType<ByteBuf, BlockStatePalette> buildUnit(String key) {
			return UnitType.create(key, type -> new BlockStatePalette(type, map, map.values().intStream().sum()));
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final UnitType<ByteBuf, BlockStatePalette> EMPTY = builder().buildUnit("empty");

	public static final CustomRegistry<ByteBuf, BlockStatePalette> REGISTRY = CustomRegistry.<ByteBuf, BlockStatePalette>builder("block_state_palette")
		.defaultType(TYPE)
		.customCodec(_ -> KLibCodecs.or(List.of(
			MCCodecs.BLOCK_STATE.flatComapMap(BlockStatePalette::new, palette -> {
				if (palette.map.size() == 1) {
					return DataResult.success(palette.map.reference2IntEntrySet().iterator().next().getKey());
				} else {
					return NOT_SINGLE_BLOCK;
				}
			}),
			MCCodecs.STATE_TO_INT_MAP.flatComapMap(BlockStatePalette::new, palette -> DataResult.success(palette.map))
		)))
		.build();

	public static final Codec<Ref<BlockStatePalette>> CODEC = REGISTRY.codec();
	public static final StreamCodec<ByteBuf, Ref<BlockStatePalette>> STREAM_CODEC = REGISTRY.streamCodec();
	public static final DataType<Ref<BlockStatePalette>> DATA_TYPE = REGISTRY.dataType();

	public static void builtInTypes(CustomRegistryTypeCollector<ByteBuf, BlockStatePalette> collector) {
		collector.register(EMPTY);
	}

	public BlockStatePalette(Reference2IntMap<BlockState> map) {
		this(null, map, map.values().intStream().sum());
	}

	public BlockStatePalette(BlockState state) {
		this(null, new Reference2IntLinkedOpenHashMap<>(Map.of(state, 1)), 1);
	}

	@Override
	public CustomRegistry<ByteBuf, BlockStatePalette> getRegistry() {
		return REGISTRY;
	}

	@Override
	public CustomRegistryType<ByteBuf, BlockStatePalette> type() {
		return TYPE;
	}

	public BlockState get(int at) {
		for (var entry : map.reference2IntEntrySet()) {
			at -= entry.getIntValue();

			if (at < 0) {
				return entry.getKey();
			}
		}

		return Blocks.AIR.defaultBlockState();
	}

	public BlockState sample(RandomSource random) {
		return get(random.nextInt(totalWeight));
	}
}
