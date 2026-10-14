package dev.latvian.mods.klib.kvector;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.entity.EntityUtils;
import dev.latvian.mods.klib.entity.PositionType;
import dev.latvian.mods.klib.entity.filter.EntityFilter;
import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record FollowingEntityKVector(Ref<EntityFilter> entity, PositionType positionType) implements KVector {
	public static final DynamicType<RegistryFriendlyByteBuf, KVector> TYPE = DynamicType.create(
		"following_entity",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityFilter.CODEC.fieldOf("entity").forGetter(FollowingEntityKVector::entity),
			PositionType.CODEC.optionalFieldOf("position_type", PositionType.CENTER).forGetter(FollowingEntityKVector::positionType)
		).apply(instance, FollowingEntityKVector::new)),
		CompositeStreamCodec.of(
			EntityFilter.STREAM_CODEC, FollowingEntityKVector::entity,
			PositionType.STREAM_CODEC, FollowingEntityKVector::positionType,
			FollowingEntityKVector::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KVector> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Vec3 get(KNumberContext ctx) {
		var e = entity.value().getFirst(ctx.level);
		return e == null ? null : EntityUtils.getPosition(e, positionType);
	}
}
