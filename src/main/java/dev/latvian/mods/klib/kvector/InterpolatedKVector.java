package dev.latvian.mods.klib.kvector;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.knumber.KNumber;
import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.EasingType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record InterpolatedKVector(Ref<KNumber> progress, EasingType ease, Ref<KVector> from, Ref<KVector> to) implements KVector {
	public static final DynamicType<RegistryFriendlyByteBuf, KVector> TYPE = DynamicType.create(
		"interpolated",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.optionalFieldOf("progress", KNumber.PROGRESS).forGetter(InterpolatedKVector::progress),
			EasingType.CODEC.optionalFieldOf("ease", EasingType.LINEAR).forGetter(InterpolatedKVector::ease),
			KVector.CODEC.fieldOf("from").forGetter(InterpolatedKVector::from),
			KVector.CODEC.fieldOf("to").forGetter(InterpolatedKVector::to)
		).apply(instance, InterpolatedKVector::new)),
		CompositeStreamCodec.of(
			KLibStreamCodecs.optional(KNumber.STREAM_CODEC, KNumber.PROGRESS), InterpolatedKVector::progress,
			MCStreamCodecs.EASING_TYPE, InterpolatedKVector::ease,
			KVector.STREAM_CODEC, InterpolatedKVector::from,
			KVector.STREAM_CODEC, InterpolatedKVector::to,
			InterpolatedKVector::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KVector> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Vec3 get(KNumberContext ctx) {
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

		return a.lerp(b, Math.clamp(ease.apply((float) progress), 0D, 1D));
	}
}
