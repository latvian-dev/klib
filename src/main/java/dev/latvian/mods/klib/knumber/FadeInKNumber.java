package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.EasingType;
import org.jetbrains.annotations.Nullable;

public record FadeInKNumber(Ref<KNumber> progress, Ref<KNumber> at, EasingType ease) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"fade_in",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.optionalFieldOf("progress", KNumber.PROGRESS).forGetter(FadeInKNumber::progress),
			KNumber.CODEC.optionalFieldOf("at", KNumber.HALF).forGetter(FadeInKNumber::at),
			EasingType.CODEC.optionalFieldOf("ease", EasingType.LINEAR).forGetter(FadeInKNumber::ease)
		).apply(instance, FadeInKNumber::new)),
		CompositeStreamCodec.of(
			KNumber.STREAM_CODEC, FadeInKNumber::progress,
			KNumber.STREAM_CODEC, FadeInKNumber::at,
			MCStreamCodecs.EASING_TYPE, FadeInKNumber::ease,
			FadeInKNumber::new
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

		if (progress >= at) {
			return 1D;
		} else {
			return (double) ease.apply((float) (Math.max(progress, 0D) / at));
		}
	}
}
