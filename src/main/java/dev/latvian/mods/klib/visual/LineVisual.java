package dev.latvian.mods.klib.visual;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.knumber.KNumber;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record LineVisual(VisualVertex start, VisualVertex end, Ref<KNumber> progress) implements Visual {
	public static final DynamicType<RegistryFriendlyByteBuf, Visual> TYPE = DynamicType.create(
		"line",
		RecordCodecBuilder.mapCodec(i -> i.group(
			VisualVertex.CODEC.fieldOf("start").forGetter(LineVisual::start),
			VisualVertex.CODEC.fieldOf("end").forGetter(LineVisual::end),
			KNumber.CODEC.optionalFieldOf("progress", KNumber.PROGRESS).forGetter(LineVisual::progress)
		).apply(i, LineVisual::new)),
		CompositeStreamCodec.of(
			VisualVertex.STREAM_CODEC, LineVisual::start,
			VisualVertex.STREAM_CODEC, LineVisual::end,
			KLibStreamCodecs.optional(KNumber.STREAM_CODEC, KNumber.PROGRESS), LineVisual::progress,
			LineVisual::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, Visual> type() {
		return TYPE;
	}
}
