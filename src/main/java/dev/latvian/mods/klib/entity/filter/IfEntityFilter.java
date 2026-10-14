package dev.latvian.mods.klib.entity.filter;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.entity.number.EntityNumber;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.util.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public record IfEntityFilter(Ref<EntityNumber> ifValue, Comparison comparison, Ref<EntityNumber> testValue) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"if",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityNumber.CODEC.fieldOf("if").forGetter(IfEntityFilter::ifValue),
			Comparison.DATA_TYPE.codec().optionalFieldOf("comparison", Comparison.NOT_EQUALS).forGetter(IfEntityFilter::comparison),
			EntityNumber.CODEC.optionalFieldOf("value", EntityNumber.ZERO).forGetter(IfEntityFilter::testValue)
		).apply(instance, IfEntityFilter::new)),
		CompositeStreamCodec.of(
			EntityNumber.STREAM_CODEC, IfEntityFilter::ifValue,
			Comparison.DATA_TYPE.streamCodec(), IfEntityFilter::comparison,
			KLibStreamCodecs.optional(EntityNumber.STREAM_CODEC, EntityNumber.ZERO), IfEntityFilter::testValue,
			IfEntityFilter::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		return comparison.test(ifValue.value().applyAsDouble(entity), testValue.value().applyAsDouble(entity));
	}
}
