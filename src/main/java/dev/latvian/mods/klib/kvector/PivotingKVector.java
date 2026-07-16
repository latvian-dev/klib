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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record PivotingKVector(
	Ref<KNumber> progress,
	Ref<KVector> target,
	Ref<KNumber> distance,
	EasingType ease,
	Ref<KNumber> startAngle,
	Ref<KNumber> addedAngle,
	Ref<KNumber> height
) implements KVector {
	public static final DynamicType<RegistryFriendlyByteBuf, KVector> TYPE = DynamicType.create(
		"pivoting",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.optionalFieldOf("progress", KNumber.PROGRESS).forGetter(PivotingKVector::progress),
			KVector.CODEC.optionalFieldOf("target", KVector.SOURCE).forGetter(PivotingKVector::target),
			KNumber.CODEC.fieldOf("distance").forGetter(PivotingKVector::distance),
			EasingType.CODEC.optionalFieldOf("ease", EasingType.LINEAR).forGetter(PivotingKVector::ease),
			KNumber.CODEC.fieldOf("start_angle").forGetter(PivotingKVector::startAngle),
			KNumber.CODEC.optionalFieldOf("added_angle", KNumber.ZERO).forGetter(PivotingKVector::addedAngle),
			KNumber.CODEC.optionalFieldOf("height", KNumber.ZERO).forGetter(PivotingKVector::height)
		).apply(instance, PivotingKVector::new)),
		CompositeStreamCodec.of(
			KLibStreamCodecs.optional(KNumber.STREAM_CODEC, KNumber.PROGRESS), PivotingKVector::progress,
			KVector.STREAM_CODEC, PivotingKVector::target,
			KNumber.STREAM_CODEC, PivotingKVector::distance,
			MCStreamCodecs.EASING_TYPE, PivotingKVector::ease,
			KNumber.STREAM_CODEC, PivotingKVector::startAngle,
			KNumber.STREAM_CODEC, PivotingKVector::addedAngle,
			KNumber.STREAM_CODEC, PivotingKVector::height,
			PivotingKVector::new
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

		Double start = startAngle.value().get(ctx);

		if (start == null) {
			return null;
		}

		Double dist = distance.value().get(ctx);

		if (dist == null) {
			return null;
		}

		double angle = Math.toRadians(Mth.rotLerp(ease.apply((float) progress), start, start + addedAngle.value().getOr(ctx, 0D)));

		var pos = target.value().get(ctx);
		return pos == null ? null : pos.add(Math.cos(angle) * dist, height.value().getOr(ctx, 0D), Math.sin(angle) * dist);
	}
}
