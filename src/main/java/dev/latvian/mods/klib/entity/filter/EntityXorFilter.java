package dev.latvian.mods.klib.entity.filter;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public record EntityXorFilter(Ref<EntityFilter> a, Ref<EntityFilter> b) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"xor",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityFilter.CODEC.fieldOf("a").forGetter(EntityXorFilter::a),
			EntityFilter.CODEC.fieldOf("b").forGetter(EntityXorFilter::b)
		).apply(instance, EntityXorFilter::new)),
		CompositeStreamCodec.of(
			EntityFilter.STREAM_CODEC, EntityXorFilter::a,
			EntityFilter.STREAM_CODEC, EntityXorFilter::b,
			EntityXorFilter::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		return a.value().test(entity) ^ b.value().test(entity);
	}
}
