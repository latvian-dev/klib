package dev.latvian.mods.klib.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.codec.KLibCodecErrors;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Reference2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record BlockStatePalette(
	Reference2IntMap<BlockState> map,
	@Nullable BlockState singleState,
	int totalWeight
) {
	private static final DataResult<BlockState> NOT_SINGLE_BLOCK = KLibCodecErrors.error("Not a single block");

	public static class Builder {
		private final Reference2IntMap<BlockState> map = new Reference2IntLinkedOpenHashMap<>(1);

		public Builder add(BlockState state, int weight) {
			map.put(state, weight);
			return this;
		}

		public BlockStatePalette build() {
			return new BlockStatePalette(map);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final BlockStatePalette EMPTY = builder().build();

	public static final Codec<BlockStatePalette> CODEC = KLibCodecs.or(
		MCCodecs.BLOCK_STATE.flatComapMap(BlockStatePalette::new, palette -> {
			if (palette.map.size() == 1) {
				return DataResult.success(palette.map.reference2IntEntrySet().iterator().next().getKey());
			} else {
				return NOT_SINGLE_BLOCK;
			}
		}),
		MCCodecs.STATE_TO_INT_MAP.flatComapMap(BlockStatePalette::new, palette -> DataResult.success(palette.map))
	);

	public static final StreamCodec<ByteBuf, BlockStatePalette> STREAM_CODEC = MCStreamCodecs.STATE_TO_INT_MAP.map(BlockStatePalette::new, BlockStatePalette::map);

	public BlockStatePalette(Reference2IntMap<BlockState> map) {
		this(map, map.size() == 1 ? map.keySet().iterator().next() : null, map.values().intStream().sum());
	}

	public BlockStatePalette(BlockState state) {
		this(new Reference2IntLinkedOpenHashMap<>(Map.of(state, 1)), state, 1);
	}

	public BlockState get(int at) {
		if (singleState != null) {
			return singleState;
		}

		for (var entry : map.reference2IntEntrySet()) {
			at -= entry.getIntValue();

			if (at < 0) {
				return entry.getKey();
			}
		}

		return Blocks.AIR.defaultBlockState();
	}

	public BlockState sample(RandomSource random) {
		if (singleState != null) {
			return singleState;
		}

		return get(random.nextInt(totalWeight));
	}
}
