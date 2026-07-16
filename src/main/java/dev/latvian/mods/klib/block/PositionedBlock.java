package dev.latvian.mods.klib.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.block.collection.BlockCollection;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.util.BlockUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public record PositionedBlock(BlockPos pos, BlockState state) implements BlockCollection {
	public static final MapCodec<PositionedBlock> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockPos.CODEC.fieldOf("pos").forGetter(PositionedBlock::pos),
		MCCodecs.BLOCK_STATE.fieldOf("state").forGetter(PositionedBlock::state)
	).apply(instance, PositionedBlock::new));

	public static final Codec<PositionedBlock> CODEC = MAP_CODEC.codec();

	public static final StreamCodec<ByteBuf, PositionedBlock> STREAM_CODEC = CompositeStreamCodec.of(
		BlockPos.STREAM_CODEC, PositionedBlock::pos,
		MCStreamCodecs.BLOCK_STATE, PositionedBlock::state,
		PositionedBlock::new
	);

	public static final StreamCodec<ByteBuf, List<PositionedBlock>> LIST_STREAM_CODEC = KLibStreamCodecs.listOf(STREAM_CODEC);

	public static final DataType<PositionedBlock> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC);
	public static final DataType<List<PositionedBlock>> LIST_DATA_TYPE = DATA_TYPE.listOf();

	public static final DynamicType<ByteBuf, BlockCollection> TYPE = DynamicType.create(
		"block",
		MAP_CODEC,
		STREAM_CODEC
	);

	@Override
	public String toString() {
		return "PositionedBlock[%d,%d,%d,%s]".formatted(pos.getX(), pos.getY(), pos.getZ(), BlockUtils.toString(state));
	}
}
