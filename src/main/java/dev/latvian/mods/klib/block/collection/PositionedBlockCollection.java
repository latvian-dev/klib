package dev.latvian.mods.klib.block.collection;

import dev.latvian.mods.klib.block.PositionedBlock;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.RandomSource;

import java.util.List;

public record PositionedBlockCollection(List<PositionedBlock> blocks) implements BlockCollection {
	public static final DynamicType<ByteBuf, BlockCollection> TYPE = DynamicType.create(
		"blocks",
		"blocks",
		PositionedBlock.CODEC.listOf(),
		KLibStreamCodecs.listOf(PositionedBlock.STREAM_CODEC),
		PositionedBlockCollection::new,
		PositionedBlockCollection::blocks
	);

	@Override
	public List<PositionedBlock> collectBlocks(RandomSource random) {
		return blocks;
	}

	@Override
	public void forEach(BlockCollectionCallback callback, RandomSource random) {
		for (var block : blocks) {
			block.forEach(callback, random);
		}
	}
}
