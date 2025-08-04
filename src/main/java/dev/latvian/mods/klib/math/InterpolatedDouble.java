package dev.latvian.mods.klib.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.data.DataType;
import net.minecraft.network.codec.ByteBufCodecs;

public record InterpolatedDouble(long start, long end, double from, double to) {
	public static final InterpolatedDouble DEFAULT = new InterpolatedDouble(0L, 0L, 0D, 0D);

	public static final DataType<InterpolatedDouble> DATA_TYPE = DataType.of(RecordCodecBuilder.create(instance -> instance.group(
		Codec.LONG.fieldOf("start").forGetter(InterpolatedDouble::start),
		Codec.LONG.fieldOf("end").forGetter(InterpolatedDouble::end),
		Codec.DOUBLE.fieldOf("from").forGetter(InterpolatedDouble::from),
		Codec.DOUBLE.fieldOf("to").forGetter(InterpolatedDouble::to)
	).apply(instance, InterpolatedDouble::new)), CompositeStreamCodec.of(
		ByteBufCodecs.VAR_LONG, InterpolatedDouble::start,
		ByteBufCodecs.VAR_LONG, InterpolatedDouble::end,
		ByteBufCodecs.DOUBLE, InterpolatedDouble::from,
		ByteBufCodecs.DOUBLE, InterpolatedDouble::to,
		InterpolatedDouble::new
	), InterpolatedDouble.class);

	public double get(long now, double delta) {
		if (now > end) {
			return to;
		} else if (now < start) {
			return from;
		}

		return KMath.clerp(((now - start - 1L) + delta) / (double) (end - start), from, to);
	}
}