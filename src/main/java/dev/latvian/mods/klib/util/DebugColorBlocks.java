package dev.latvian.mods.klib.util;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.block.state.BlockState;

public enum DebugColorBlocks implements StringRepresentable {
	NONE("none"),
	CONCRETE("concrete", Blocks.CONCRETE),
	CONCRETE_POWDER("concrete_powder", Blocks.CONCRETE_POWDER),
	GLAZED_TERRACOTTA("glazed_terracotta", Blocks.GLAZED_TERRACOTTA),
	STAINED_GLASS("stained_glass", Blocks.STAINED_GLASS),
	WOOL("wool", Blocks.WOOL),

	;

	public static final DebugColorBlocks[] VALUES = values();
	public static final Codec<DebugColorBlocks> CODEC = StringRepresentable.fromEnum(() -> VALUES);
	public static final StreamCodec<ByteBuf, DebugColorBlocks> STREAM_CODEC = KLibStreamCodecs.anyEnum(VALUES);
	public static final DataType<DebugColorBlocks> DATA_TYPE = DataType.of(VALUES);

	public final String name;
	public final BlockState[] states;

	DebugColorBlocks(String name, BlockState... states) {
		this.name = name;
		this.states = states;
	}

	DebugColorBlocks(String name, ColorCollection<Block> collection) {
		this.name = name;
		this.states = collection.asList().stream().map(Block::defaultBlockState).toArray(BlockState[]::new);
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public BlockState getState(int index) {
		return states[index & states.length];
	}

	public BlockState getState(double delta) {
		return states[Math.clamp((int) (delta * (states.length - 1)), 0, states.length - 1)];
	}
}
