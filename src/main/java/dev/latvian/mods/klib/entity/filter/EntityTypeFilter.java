package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

public record EntityTypeFilter(EntityType<?> entityType) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"entity_type",
		"entity_type",
		BuiltInRegistries.ENTITY_TYPE.byNameCodec(),
		ByteBufCodecs.registry(Registries.ENTITY_TYPE),
		EntityTypeFilter::new,
		EntityTypeFilter::entityType,
		EntityTypes.PLAYER
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		return entity.getType() == entityType;
	}
}
