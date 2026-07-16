package dev.latvian.mods.klib.block.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.util.BlockUtils;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public interface BlockFilter extends BlockPredicate, CustomRegistryValue<RegistryFriendlyByteBuf, BlockFilter> {
	static UnitType<RegistryFriendlyByteBuf, BlockFilter> simple(String id, SimpleBlockPredicate predicate) {
		return UnitType.create(id, type -> SimpleBlockFilter.simple(type, predicate));
	}

	UnitType<RegistryFriendlyByteBuf, BlockFilter> NONE = UnitType.create("none", NoneBlockFilter.INSTANCE);
	UnitType<RegistryFriendlyByteBuf, BlockFilter> ANY = UnitType.create("any", AnyBlockFilter.INSTANCE);

	UnitType<RegistryFriendlyByteBuf, BlockFilter> VISIBLE = simple("visible", (_, _, state) -> BlockUtils.isVisible(state));
	UnitType<RegistryFriendlyByteBuf, BlockFilter> PARTIAL = simple("partial", (_, _, state) -> BlockUtils.isPartial(state));
	UnitType<RegistryFriendlyByteBuf, BlockFilter> EXPOSED = simple("exposed", BlockUtils::isBlockExposed);
	UnitType<RegistryFriendlyByteBuf, BlockFilter> FLUID = simple("fluid", (_, _, state) -> BlockUtils.isFluid(state));

	static BlockFilter of(boolean value) {
		return value ? AnyBlockFilter.INSTANCE : NoneBlockFilter.INSTANCE;
	}

	CustomRegistry<RegistryFriendlyByteBuf, BlockFilter> REGISTRY = CustomRegistry.<RegistryFriendlyByteBuf, BlockFilter>builder("block_filter")
		.customCodec(direct -> KLibCodecs.or(List.of(
			Codec.BOOL.flatComapMap(BlockFilter::of, filter -> {
				if (filter == of(true)) {
					return DataResult.success(true);
				} else if (filter == of(false)) {
					return DataResult.success(false);
				} else {
					return DataResult.error(() -> "Expected either 'any' or 'none'");
				}
			}),
			Codec.STRING.<BlockFilter>flatXmap(s -> {
				try {
					var state = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK, s, false).blockState();

					if (!state.isAir()) {
						if (s.indexOf('[') != -1) {
							return DataResult.success(new BlockStateFilter(state));
						} else {
							return DataResult.success(new BlockIdFilter(state.getBlock()));
						}
					}
				} catch (Exception ignore) {
				}

				return DataResult.error(() -> "Invalid blockstate format: " + s);
			}, filter -> switch (filter) {
				case BlockStateFilter f -> DataResult.success(BlockUtils.toString(f.blockState()) + (f.blockState() == f.blockState().getBlock().defaultBlockState() ? "[]" : ""));
				case BlockIdFilter f -> DataResult.success(f.block().builtInRegistryHolder().getKey().identifier().toString());
				case null, default -> DataResult.error(() -> "");
			}),
			direct
		)))
		.build();

	Codec<Ref<BlockFilter>> CODEC = REGISTRY.codec();
	StreamCodec<RegistryFriendlyByteBuf, Ref<BlockFilter>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<BlockFilter>> DATA_TYPE = REGISTRY.dataType();

	static void builtInTypes(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, BlockFilter> registry) {
		registry.register(NONE);
		registry.register(ANY);

		registry.register(VISIBLE);
		registry.register(PARTIAL);
		registry.register(EXPOSED);
		registry.register(FLUID);

		registry.register(BlockNotFilter.TYPE);
		registry.register(BlockAndFilter.TYPE);
		registry.register(BlockOrFilter.TYPE);
		registry.register(BlockXorFilter.TYPE);

		registry.register(BlockIdFilter.TYPE);
		registry.register(BlockStateFilter.TYPE);
		registry.register(BlockTypeTagFilter.TYPE);
	}

	@Override
	default CustomRegistry<RegistryFriendlyByteBuf, BlockFilter> getRegistry() {
		return REGISTRY;
	}

	default BlockFilter not() {
		return new BlockNotFilter(ref());
	}

	default BlockFilter and(BlockFilter filter) {
		if (filter == of(true)) {
			return this;
		} else if (filter == of(false)) {
			return filter;
		} else {
			return new BlockAndFilter(List.of(ref(), filter.ref()));
		}
	}

	default BlockFilter or(BlockFilter filter) {
		if (filter == of(true)) {
			return filter;
		} else if (filter == of(false)) {
			return this;
		} else {
			return new BlockOrFilter(List.of(ref(), filter.ref()));
		}
	}
}
