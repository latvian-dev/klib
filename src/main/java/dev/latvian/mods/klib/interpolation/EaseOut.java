package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.registry.UnitType;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * <a href="https://easings.net/">Source</a>
 */
public enum EaseOut implements Interpolation, StringRepresentable {
	SINE("sine", x -> (float) Math.sin(x * Math.PI / 2D)),
	QUAD("quad", x -> 1F - (1F - x) * (1F - x)),
	CUBIC("cubic", x -> 1F - (1F - x) * (1F - x) * (1F - x)),
	QUART("quart", x -> 1F - (1F - x) * (1F - x) * (1F - x) * (1F - x)),
	QUINT("quint", x -> 1F - (1F - x) * (1F - x) * (1F - x) * (1F - x) * (1F - x)),
	EXPO("expo", x -> x == 1F ? 1F : (float) (1D - Math.pow(2D, -10D * x))),
	CIRC("circ", x -> (float) Math.sqrt(1D - (x - 1D) * (x - 1D))),
	BACK("back", x -> 1F - (1F - x) * (1F - x) * (2.70158F * (1F - x) - 1.70158F)),
	ELASTIC("elastic", x -> (float) (Math.sin(-13D * Math.PI / 2D * (x + 1D)) * Math.pow(2D, -10D * x) + 1D)),
	BOUNCE("bounce", x -> {
		if (x < 1F / 2.75F) {
			return 7.5625F * x * x;
		} else if (x < 2F / 2.75F) {
			return 7.5625F * (x -= 1.5F / 2.75F) * x + 0.75F;
		} else if (x < 2.5D / 2.75D) {
			return 7.5625F * (x -= 2.25F / 2.75F) * x + 0.9375F;
		} else {
			return 7.5625F * (x -= 2.625F / 2.75F) * x + 0.984375F;
		}
	});

	public static final EaseOut[] VALUES = values();
	public static final Codec<EaseOut> CODEC = StringRepresentable.fromEnum(() -> VALUES);
	public static final StreamCodec<ByteBuf, EaseOut> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(i -> VALUES[i], EaseOut::ordinal);

	public final String name;
	public final Float2FloatFunction function;
	public final UnitType<ByteBuf, Interpolation> type;

	EaseOut(String name, Float2FloatFunction function) {
		this.name = name;
		this.function = function;
		this.type = UnitType.create(name + "_out", this);
	}

	@Override
	public UnitType<ByteBuf, Interpolation> type() {
		return type;
	}

	@Override
	public float interpolate(float x) {
		return function.get(x);
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
