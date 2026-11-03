package dev.latvian.mods.klib.volume;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.math.AAIBB;
import dev.latvian.mods.klib.registry.DynamicType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

import java.util.stream.Stream;

public record BlockVolume(BlockPos start, BlockPos end, AABB box, AAIBB intBox) implements Volume {
	public static BlockVolume of(BlockPos start, BlockPos end) {
		var start0 = BlockPos.min(start, end);
		var end0 = BlockPos.max(start, end);
		return new BlockVolume(start0, end0, AABB.encapsulatingFullBlocks(start0, end0), new AAIBB(start0, end0));
	}

	public static BlockVolume of(BlockPos pos) {
		return of(pos, pos);
	}

	public static BlockVolume of(AAIBB intBox) {
		return new BlockVolume(intBox.min(), intBox.max(), intBox.aabb(), intBox);
	}

	public static final DynamicType<ByteBuf, Volume> TYPE = DynamicType.create(
		"block",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockPos.CODEC.fieldOf("start").forGetter(BlockVolume::start),
			BlockPos.CODEC.fieldOf("end").forGetter(BlockVolume::end)
		).apply(instance, BlockVolume::of)),
		CompositeStreamCodec.of(
			BlockPos.STREAM_CODEC, BlockVolume::start,
			BlockPos.STREAM_CODEC, BlockVolume::end,
			BlockVolume::of
		)
	);

	@Override
	public DynamicType<ByteBuf, Volume> type() {
		return TYPE;
	}

	@Override
	public boolean contains(int x, int y, int z) {
		return intBox.contains(x, y, z);
	}

	@Override
	public Stream<BlockPos> getBlocks() {
		return BlockPos.betweenClosedStream(start, end);
	}

	@Override
	public Volume move(double x, double y, double z) {
		int ix = Mth.floor(x);
		int iy = Mth.floor(y);
		int iz = Mth.floor(z);

		if (ix == x && iy == y && iz == z) {
			return of(start.offset(ix, iy, iz), end.offset(ix, iy, iz));
		}

		return Volume.super.move(x, y, z);
	}
}
