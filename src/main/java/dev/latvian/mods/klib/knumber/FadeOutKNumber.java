package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.EasingType;
import org.jetbrains.annotations.Nullable;

public record FadeOutKNumber(Ref<KNumber> progress, Ref<KNumber> at, EasingType ease) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"fade_out",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.optionalFieldOf("progress", KNumber.PROGRESS).forGetter(FadeOutKNumber::progress),
			KNumber.CODEC.optionalFieldOf("at", KNumber.HALF).forGetter(FadeOutKNumber::at),
			EasingType.CODEC.optionalFieldOf("ease", EasingType.LINEAR).forGetter(FadeOutKNumber::ease)
		).apply(instance, FadeOutKNumber::new)),
		CompositeStreamCodec.of(
			KNumber.STREAM_CODEC, FadeOutKNumber::progress,
			KNumber.STREAM_CODEC, FadeOutKNumber::at,
			MCStreamCodecs.EASING_TYPE, FadeOutKNumber::ease,
			FadeOutKNumber::new
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

		var at = this.at.value().getOrNaN(ctx);

		if (Double.isNaN(at)) {
			return null;
		}

		if (progress <= at) {
			return 1D;
		} else {
			return (double) ease.apply((float) (1D - (Math.min(progress, 1D) - at) / (1D - at)));
		}
	}
}
