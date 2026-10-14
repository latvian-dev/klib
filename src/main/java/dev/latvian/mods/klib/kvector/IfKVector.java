package dev.latvian.mods.klib.kvector;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.knumber.KNumber;
import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.util.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record IfKVector(
	Ref<KNumber> ifValue,
	Comparison comparison,
	Ref<KNumber> testValue,
	Optional<Ref<KVector>> thenValue,
	Optional<Ref<KVector>> elseValue
) implements KVector {
	public static final DynamicType<RegistryFriendlyByteBuf, KVector> TYPE = DynamicType.create(
		"if",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.fieldOf("if").forGetter(IfKVector::ifValue),
			Comparison.DATA_TYPE.codec().optionalFieldOf("comparison", Comparison.NOT_EQUALS).forGetter(IfKVector::comparison),
			KNumber.CODEC.optionalFieldOf("value", KNumber.ZERO).forGetter(IfKVector::testValue),
			KVector.CODEC.optionalFieldOf("then").forGetter(IfKVector::thenValue),
			KVector.CODEC.optionalFieldOf("else").forGetter(IfKVector::elseValue)
		).apply(instance, IfKVector::new)),
		CompositeStreamCodec.of(
			KNumber.STREAM_CODEC, IfKVector::ifValue,
			Comparison.DATA_TYPE.streamCodec(), IfKVector::comparison,
			KLibStreamCodecs.optional(KNumber.STREAM_CODEC, KNumber.ZERO), IfKVector::testValue,
			ByteBufCodecs.optional(KVector.STREAM_CODEC), IfKVector::thenValue,
			ByteBufCodecs.optional(KVector.STREAM_CODEC), IfKVector::elseValue,
			IfKVector::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KVector> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Vec3 get(KNumberContext ctx) {
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
