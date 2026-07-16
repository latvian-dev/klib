package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record ClampedKNumber(Ref<KNumber> value, Ref<KNumber> min, Ref<KNumber> max) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"clamped",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.fieldOf("value").forGetter(ClampedKNumber::value),
			KNumber.CODEC.optionalFieldOf("min", KNumber.ZERO).forGetter(ClampedKNumber::min),
			KNumber.CODEC.optionalFieldOf("max", KNumber.ONE).forGetter(ClampedKNumber::max)
		).apply(instance, ClampedKNumber::new)),
		CompositeStreamCodec.of(
			KNumber.STREAM_CODEC, ClampedKNumber::value,
			KLibStreamCodecs.optional(KNumber.STREAM_CODEC, KNumber.ZERO), ClampedKNumber::min,
			KLibStreamCodecs.optional(KNumber.STREAM_CODEC, KNumber.ONE), ClampedKNumber::max,
			ClampedKNumber::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KNumber> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Double get(KNumberContext ctx) {
		var value = this.value.value().getOrNaN(ctx);
		var min = this.min.value().getOrNaN(ctx);
		var max = this.max.value().getOrNaN(ctx);

		if (Double.isNaN(value)) {
			return null;
		}

		if (!Double.isNaN(min)) {
			value = Math.max(value, min);
		}

		if (!Double.isNaN(max)) {
			value = Math.min(value, max);
		}

		return value;
	}
}
