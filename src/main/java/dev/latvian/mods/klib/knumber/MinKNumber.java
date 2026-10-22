package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record MinKNumber(Ref<KNumber> a, Ref<KNumber> b) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"min",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.fieldOf("a").forGetter(MinKNumber::a),
			KNumber.CODEC.fieldOf("b").forGetter(MinKNumber::b)
		).apply(instance, MinKNumber::new)),
		CompositeStreamCodec.of(
			KNumber.STREAM_CODEC, MinKNumber::a,
			KNumber.STREAM_CODEC, MinKNumber::b,
			MinKNumber::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KNumber> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Double get(KNumberContext ctx) {
		var a = this.a.value().getOrNaN(ctx);

		if (Double.isNaN(a)) {
			return null;
		}

		var b = this.b.value().getOrNaN(ctx);

		if (Double.isNaN(b)) {
			return null;
		}

		return Math.min(a, b);
	}
}