package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record Atan2KNumber(Ref<KNumber> x, Ref<KNumber> y) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"atan2",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.optionalFieldOf("x", KNumber.ZERO).forGetter(Atan2KNumber::x),
			KNumber.CODEC.optionalFieldOf("y", KNumber.ONE).forGetter(Atan2KNumber::y)
		).apply(instance, Atan2KNumber::new)),
		CompositeStreamCodec.of(
			KLibStreamCodecs.optional(KNumber.STREAM_CODEC, KNumber.ZERO), Atan2KNumber::x,
			KLibStreamCodecs.optional(KNumber.STREAM_CODEC, KNumber.ONE), Atan2KNumber::y,
			Atan2KNumber::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KNumber> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Double get(KNumberContext ctx) {
		var x = this.x.value().get(ctx);
		var y = this.y.value().get(ctx);

		if (x == null || y == null) {
			return null;
		}

		return Math.toDegrees(Math.atan2(y, x));
	}
}
