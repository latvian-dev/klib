package dev.latvian.mods.klib.block.collection;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.block.PositionedBlock;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public interface BlockCollection extends CustomRegistryValue<ByteBuf, BlockCollection> {
	UnitType<ByteBuf, BlockCollection> EMPTY = UnitType.create("empty", EmptyBlockCollection.INSTANCE);
	CustomRegistry<ByteBuf, BlockCollection> REGISTRY = CustomRegistry.create("block_collection", PositionedBlock.TYPE);
	Codec<Ref<BlockCollection>> CODEC = REGISTRY.codec();
	StreamCodec<ByteBuf, Ref<BlockCollection>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<BlockCollection>> DATA_TYPE = REGISTRY.dataType();

	static void builtInTypes(CustomRegistryTypeCollector<ByteBuf, BlockCollection> collector) {
		collector.register(EMPTY);
		collector.register(PositionedBlockCollection.TYPE);
		collector.register(PositionedBlockStatePalette.TYPE);
	}

	@Override
	default CustomRegistry<ByteBuf, BlockCollection> getRegistry() {
		return REGISTRY;
	}

	default List<PositionedBlock> collectBlocks(RandomSource random) {
		var list = new ArrayList<PositionedBlock>(1);
		forEach((pos, state) -> list.add(new PositionedBlock(pos, state)), random);
		return list;
	}

	void forEach(BlockCollectionCallback callback, RandomSource random);
}
