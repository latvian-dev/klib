package dev.latvian.mods.klib.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record Cursor(Vec3 pos, BlockPos blockPos, Direction direction) {
	@Nullable
	public static Cursor fromNBT(@Nullable Tag tag) {
		if (!(tag instanceof CompoundTag nbt)) {
			return null;
		}

		var dx = nbt.getDoubleOr("x", 0.0);
		var dy = nbt.getDoubleOr("y", 0.0);
		var dz = nbt.getDoubleOr("z", 0.0);
		var floorPos = BlockPos.of(nbt.getLongOr("block", 0L));
		var direction = Direction.from3DDataValue(nbt.getByteOr("dir", (byte) 0));
		return new Cursor(KMath.vec3(dx, dy, dz), floorPos, direction);
	}

	public Cursor(BlockHitResult hit) {
		this(hit.getLocation(), hit.getBlockPos(), hit.getDirection());
	}

	public BlockPos getOffsetBlockPos() {
		return blockPos.relative(direction);
	}

	public Vec3 getBottomCenter() {
		return Vec3.atBottomCenterOf(getOffsetBlockPos());
	}

	public Vec3 getCenter() {
		return Vec3.atCenterOf(getOffsetBlockPos());
	}

	public CompoundTag toNBT(CompoundTag nbt) {
		nbt.putDouble("x", pos.x);
		nbt.putDouble("y", pos.y);
		nbt.putDouble("z", pos.z);
		nbt.putLong("block", blockPos.asLong());
		nbt.putByte("dir", (byte) direction.ordinal());
		return nbt;
	}
}
