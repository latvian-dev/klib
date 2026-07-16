package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.util.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record IfKNumber(
	Ref<KNumber> ifValue,
	Comparison comparison,
	Ref<KNumber> testValue,
	Optional<Ref<KNumber>> thenValue,
	Optional<Ref<KNumber>> elseValue
) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"if",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.fieldOf("if").forGetter(IfKNumber::ifValue),
			Comparison.DATA_TYPE.codec().optionalFieldOf("comparison", Comparison.NOT_EQUALS).forGetter(IfKNumber::comparison),
			KNumber.CODEC.optionalFieldOf("value", KNumber.ZERO).forGetter(IfKNumber::testValue),
			KNumber.CODEC.optionalFieldOf("then").forGetter(IfKNumber::thenValue),
			KNumber.CODEC.optionalFieldOf("else").forGetter(IfKNumber::elseValue)
		).apply(instance, IfKNumber::new)),
		CompositeStreamCodec.of(
			KNumber.STREAM_CODEC, IfKNumber::ifValue,
			Comparison.DATA_TYPE.streamCodec(), IfKNumber::comparison,
			KLibStreamCodecs.optional(KNumber.STREAM_CODEC, KNumber.ZERO), IfKNumber::testValue,
			ByteBufCodecs.optional(KNumber.STREAM_CODEC), IfKNumber::thenValue,
			ByteBufCodecs.optional(KNumber.STREAM_CODEC), IfKNumber::elseValue,
			IfKNumber::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KNumber> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Double get(KNumberContext ctx) {
		var i = ifValue.value().get(ctx);

		if (i == null) {
			return null;
		}

		var t = testValue.value().get(ctx);

		if (t == null) {
			return null;
		}

		if (comparison.test(i, t)) {
			if (thenValue.isPresent()) {
				return thenValue.get().value().get(ctx);
			}
		} else {
			if (elseValue.isPresent()) {
				return elseValue.get().value().get(ctx);
			}
		}

		return null;
	}
}
