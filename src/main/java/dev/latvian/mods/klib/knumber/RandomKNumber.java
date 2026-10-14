package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record RandomKNumber(Ref<KNumber> min, Ref<KNumber> max) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"random",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.optionalFieldOf("min", KNumber.ZERO).forGetter(RandomKNumber::min),
			KNumber.CODEC.optionalFieldOf("max", KNumber.ONE).forGetter(RandomKNumber::max)
		).apply(instance, RandomKNumber::new)),
		CompositeStreamCodec.of(
			KLibStreamCodecs.optional(KNumber.STREAM_CODEC, KNumber.ZERO), RandomKNumber::min,
			KLibStreamCodecs.optional(KNumber.STREAM_CODEC, KNumber.ONE), RandomKNumber::max,
			RandomKNumber::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KNumber> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Double get(KNumberContext ctx) {
		var min = this.min.value().get(ctx);
		var max = this.max.value().get(ctx);

		if (min == null || max == null) {
			return null;
		}

		return ctx.level.getRandom().nextRange(min, max);
	}
}
