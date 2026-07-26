package dev.latvian.mods.klib.block.collection;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.block.BlockStatePalette;
import dev.latvian.mods.klib.block.PositionedBlock;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public record PositionedBlockStatePalette(List<BlockPos> positions, Ref<BlockStatePalette> palette) implements BlockCollection {
	public static final DynamicType<ByteBuf, BlockCollection> TYPE = DynamicType.create(
		"palette",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockPos.CODEC.listOf().optionalFieldOf("positions", List.of()).forGetter(PositionedBlockStatePalette::positions),
			BlockStatePalette.CODEC.fieldOf("palette").forGetter(PositionedBlockStatePalette::palette)
		).apply(instance, PositionedBlockStatePalette::new)),
		CompositeStreamCodec.of(
			KLibStreamCodecs.listOf(BlockPos.STREAM_CODEC), PositionedBlockStatePalette::positions,
			BlockStatePalette.STREAM_CODEC, PositionedBlockStatePalette::palette,
			PositionedBlockStatePalette::new
		)
	);

	@Override
	public List<PositionedBlock> collectBlocks(RandomSource random) {
		var p = palette.value();
		var list = new ArrayList<PositionedBlock>(positions.size());

		for (var pos : positions) {
			list.add(new PositionedBlock(pos, p.sample(random)));
		}

		return list;
	}

	@Override
	public void forEach(BlockCollectionCallback callback, RandomSource random) {
		var p = palette.value();

		for (var pos : positions) {
			callback.accept(pos, p.sample(random));
		}
	}
}
