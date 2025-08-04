package dev.latvian.mods.klib.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.data.DataType;
import net.minecraft.network.codec.ByteBufCodecs;

public record InterpolatedFloat(long start, long end, float from, float to) {
	public static final InterpolatedFloat DEFAULT = new InterpolatedFloat(0L, 0L, 0F, 0F);

	public static final DataType<InterpolatedFloat> DATA_TYPE = DataType.of(RecordCodecBuilder.create(instance -> instance.group(
		Codec.LONG.fieldOf("start").forGetter(InterpolatedFloat::start),
		Codec.LONG.fieldOf("end").forGetter(InterpolatedFloat::end),
		Codec.FLOAT.fieldOf("from").forGetter(InterpolatedFloat::from),
		Codec.FLOAT.fieldOf("to").forGetter(InterpolatedFloat::to)
	).apply(instance, InterpolatedFloat::new)), CompositeStreamCodec.of(
		ByteBufCodecs.VAR_LONG, InterpolatedFloat::start,
		ByteBufCodecs.VAR_LONG, InterpolatedFloat::end,
		ByteBufCodecs.FLOAT, InterpolatedFloat::from,
		ByteBufCodecs.FLOAT, InterpolatedFloat::to,
		InterpolatedFloat::new
	), InterpolatedFloat.class);

	public float get(long now, float delta) {
		if (now > end) {
			return to;
		} else if (now < start) {
			return from;
		}

		return KMath.clerp(((now - start - 1L) + delta) / (float) (end - start), from, to);
	}
}