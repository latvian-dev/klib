package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.util.IntOrUUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record ExactEntityFilter(IntOrUUID entityId) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"exact",
		"entity_id",
		IntOrUUID.CODEC,
		IntOrUUID.STREAM_CODEC,
		ExactEntityFilter::new,
		ExactEntityFilter::entityId
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		return entityId.testEntity(entity);
	}

	@Override
	@Nullable
	public Entity getFirst(Level level) {
		return level.klib$getEntity(entityId);
	}
}
