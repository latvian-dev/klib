package dev.latvian.mods.klib.interpolation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.util.Cast;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record InterpolationType<T extends Interpolation>(String name, MapCodec<T> mapCodec, StreamCodec<ByteBuf, T> streamCodec, @Nullable T unit) {
	public static <T extends Interpolation> InterpolationType<T> of(String name, MapCodec<T> codec, StreamCodec<ByteBuf, T> streamCodec) {
		return new InterpolationType<>(name, codec, streamCodec, null);
	}

	public static <T extends Interpolation> InterpolationType<T> unit(String name, T unit) {
		return new InterpolationType<>(name, MapCodec.unit(unit), StreamCodec.unit(unit), unit);
	}

	private static Map<String, InterpolationType<?>> MAP;

	public static Map<String, InterpolationType<?>> getMap() {
		if (MAP == null) {
			var all = new ArrayList<InterpolationType<?>>();
			all.add(LinearInterpolation.TYPE);
			all.add(FixedInterpolation.TYPE);
			all.add(ScaledInterpolation.TYPE);
			all.add(JoinedInterpolation.TYPE);
			all.add(InverseInterpolation.TYPE);
			all.add(FlipXInterpolation.TYPE);
			all.add(FlipYInterpolation.TYPE);
			all.add(BezierInterpolation.TYPE);

			for (var easing : EaseIn.VALUES) {
				all.add(easing.type);
			}

			for (var easing : EaseOut.VALUES) {
				all.add(easing.type);
			}

			all.addAll(Arrays.asList(JoinedInterpolation.EASING));

			NeoForge.EVENT_BUS.post(new InterpolationTypeRegistryEvent(all));
			MAP = Map.copyOf(all.stream().collect(Collectors.toMap(InterpolationType::name, Function.identity())));
		}

		return MAP;
	}

	public static final Codec<InterpolationType<?>> CODEC = Codec.STRING.comapFlatMap(o -> {
		var v = getMap().get(o);
		return v == null ? DataResult.error(() -> "Interpolation type '" + o + "' not found") : DataResult.success(v);
	}, InterpolationType::name);

	public static final StreamCodec<ByteBuf, InterpolationType<?>> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(s -> getMap().get(s), InterpolationType::name);

	public static final DataType<InterpolationType<?>> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, Cast.to(InterpolationType.class));
}
