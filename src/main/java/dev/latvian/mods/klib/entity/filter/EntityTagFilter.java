package dev.latvian.mods.klib.entity.filter;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.Entity;

public record EntityTagFilter(String tag) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"tag",
		"tag",
		Codec.STRING,
		ByteBufCodecs.STRING_UTF8,
		EntityTagFilter::new,
		EntityTagFilter::tag
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		return entity.entityTags().contains(tag);
	}
}
