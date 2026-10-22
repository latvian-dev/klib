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
public enum EaseIn implements Interpolation, StringRepresentable {
	SINE("sine", EasingType.IN_SINE),
	QUAD("quad", EasingType.IN_QUAD),
	CUBIC("cubic", EasingType.IN_CUBIC),
	QUART("quart", EasingType.IN_QUART),
	QUINT("quint", EasingType.IN_QUINT),
	EXPO("expo", EasingType.IN_EXPO),
	CIRC("circ", EasingType.IN_CIRC),
	BACK("back", EasingType.IN_BACK),
	ELASTIC("elastic", EasingType.IN_ELASTIC),
	BOUNCE("bounce", EasingType.IN_BOUNCE);

	public static final EaseIn[] VALUES = values();
	public static final Codec<EaseIn> CODEC = StringRepresentable.fromEnum(() -> VALUES);
	public static final StreamCodec<ByteBuf, EaseIn> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(i -> VALUES[i], EaseIn::ordinal);

	public final String name;
	public final EasingType function;
	public final UnitType<ByteBuf, Interpolation> type;

	EaseIn(String name, EasingType function) {
		this.name = name;
		this.function = function;
		this.type = UnitType.create(name + "_in", this);
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
