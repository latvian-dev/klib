package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record InDimensionEntityFilter(ResourceKey<Level> dimension) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"in_dimension",
		"dimension",
		MCCodecs.DIMENSION,
		MCStreamCodecs.DIMENSION,
		InDimensionEntityFilter::new,
		InDimensionEntityFilter::dimension
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		return entity.level().dimension() == dimension;
	}
}
