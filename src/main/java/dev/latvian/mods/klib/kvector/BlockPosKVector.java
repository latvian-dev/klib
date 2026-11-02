package dev.latvian.mods.klib.kvector;

import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.PrefersDirectCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record BlockPosKVector(BlockPos pos) implements KVector, PrefersDirectCodec {
	public static final DynamicType<RegistryFriendlyByteBuf, KVector> TYPE = DynamicType.create(
		"block",
		"pos",
		BlockPos.CODEC,
		BlockPos.STREAM_CODEC,
		BlockPosKVector::new,
		BlockPosKVector::pos
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KVector> type() {
		return TYPE;
	}

	@Override
	public Vec3 get(KNumberContext ctx) {
		return Vec3.atCenterOf(pos);
	}

	@Override
	public KVector offset(KVector other) {
		if (other instanceof BlockPosKVector v) {
			return KVector.of(pos.offset(v.pos));
		}

		return KVector.super.offset(other);
	}

	@Override
	public @NotNull String toString() {
		return pos.toString();
	}
}
