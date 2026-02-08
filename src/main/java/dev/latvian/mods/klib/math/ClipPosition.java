package dev.latvian.mods.klib.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public record ClipPosition(Vec3 position, BlockPos blockPosition, Direction side) {
	public static final Codec<ClipPosition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		MCCodecs.VEC3.fieldOf("position").forGetter(ClipPosition::position),
		BlockPos.CODEC.fieldOf("block").forGetter(ClipPosition::blockPosition),
		Direction.CODEC.fieldOf("side").forGetter(ClipPosition::side)
	).apply(instance, ClipPosition::new));

	public static final StreamCodec<ByteBuf, ClipPosition> STREAM_CODEC = CompositeStreamCodec.of(
		MCStreamCodecs.VEC3, ClipPosition::position,
		BlockPos.STREAM_CODEC, ClipPosition::blockPosition,
		Direction.STREAM_CODEC, ClipPosition::side,
		ClipPosition::new
	);

	public static final DataType<ClipPosition> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, ClipPosition.class);

	public ClipPosition(BlockHitResult hit) {
		this(hit.getLocation(), hit.getBlockPos(), hit.getDirection());
	}

	public BlockPos getOffsetBlockPosition() {
		return blockPosition.relative(side);
	}

	public Vec3 getBottomCenter() {
		return Vec3.atBottomCenterOf(getOffsetBlockPosition());
	}

	public Vec3 getCenter() {
		return Vec3.atCenterOf(getOffsetBlockPosition());
	}
}
