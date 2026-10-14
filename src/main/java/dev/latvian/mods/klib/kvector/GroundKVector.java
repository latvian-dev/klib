package dev.latvian.mods.klib.kvector;

import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.util.BlockUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record GroundKVector(Ref<KVector> vector) implements KVector {
	public static final DynamicType<RegistryFriendlyByteBuf, KVector> TYPE = DynamicType.create(
		"ground",
		"vector",
		KVector.CODEC,
		KVector.STREAM_CODEC,
		GroundKVector::new,
		GroundKVector::vector
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KVector> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Vec3 get(KNumberContext ctx) {
		var pos = vector.value().get(ctx);

		if (pos == null || ctx.level == null) {
			return null;
		}

		var groundY = BlockUtils.getGroundY(ctx.level, pos.x, pos.y, pos.z);

		if (Double.isNaN(groundY)) {
			return null;
		}

		return new Vec3(pos.x, groundY, pos.z);
	}
}
