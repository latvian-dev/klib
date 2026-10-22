package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.registry.UnitType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.EasingType;
import net.minecraft.util.StringRepresentable;

/**
 * <a href="https://easings.net/">Source</a>
 */
public enum EaseOut implements Interpolation, StringRepresentable {
	SINE("sine", EasingType.OUT_SINE),
	QUAD("quad", EasingType.OUT_QUAD),
	CUBIC("cubic", EasingType.OUT_CUBIC),
	QUART("quart", EasingType.OUT_QUART),
	QUINT("quint", EasingType.OUT_QUINT),
	EXPO("expo", EasingType.OUT_EXPO),
	CIRC("circ", EasingType.OUT_CIRC),
	BACK("back", EasingType.OUT_BACK),
	ELASTIC("elastic", EasingType.OUT_ELASTIC),
	BOUNCE("bounce", EasingType.OUT_BOUNCE);

	public static final EaseOut[] VALUES = values();
	public static final Codec<EaseOut> CODEC = StringRepresentable.fromEnum(() -> VALUES);
	public static final StreamCodec<ByteBuf, EaseOut> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(i -> VALUES[i], EaseOut::ordinal);

	public final String name;
	public final EasingType function;
	public final UnitType<ByteBuf, Interpolation> type;

	EaseOut(String name, EasingType function) {
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
		return function.apply(x);
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
