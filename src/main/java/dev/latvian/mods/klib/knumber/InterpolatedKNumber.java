package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.EasingType;
import org.jetbrains.annotations.Nullable;

public record InterpolatedKNumber(Ref<KNumber> progress, EasingType ease, Ref<KNumber> from, Ref<KNumber> to) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"interpolated",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.optionalFieldOf("progress", KNumber.PROGRESS).forGetter(InterpolatedKNumber::progress),
			EasingType.CODEC.optionalFieldOf("ease", EasingType.LINEAR).forGetter(InterpolatedKNumber::ease),
			KNumber.CODEC.fieldOf("from").forGetter(InterpolatedKNumber::from),
			KNumber.CODEC.fieldOf("to").forGetter(InterpolatedKNumber::to)
		).apply(instance, InterpolatedKNumber::new)),
		CompositeStreamCodec.of(
			KLibStreamCodecs.optional(KNumber.STREAM_CODEC, KNumber.PROGRESS), InterpolatedKNumber::progress,
			MCStreamCodecs.EASING_TYPE, InterpolatedKNumber::ease,
			KNumber.STREAM_CODEC, InterpolatedKNumber::from,
			KNumber.STREAM_CODEC, InterpolatedKNumber::to,
			InterpolatedKNumber::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KNumber> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Double get(KNumberContext ctx) {
		var progress = this.progress.value().getOrNaN(ctx);

		if (Double.isNaN(progress)) {
			return null;
		}

		if (progress <= 0D) {
			return from.value().get(ctx);
		} else if (progress >= 1D) {
			return to.value().get(ctx);
		}

		var a = from.value().get(ctx);
		var b = to.value().get(ctx);

		if (a == null || b == null) {
			return null;
		}

		return KMath.lerp(ease.apply((float) progress), a, b);
	}
}
