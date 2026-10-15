package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record MaxKNumber(Ref<KNumber> a, Ref<KNumber> b) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"max",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.fieldOf("a").forGetter(MaxKNumber::a),
			KNumber.CODEC.fieldOf("b").forGetter(MaxKNumber::b)
		).apply(instance, MaxKNumber::new)),
		CompositeStreamCodec.of(
			KNumber.STREAM_CODEC, MaxKNumber::a,
			KNumber.STREAM_CODEC, MaxKNumber::b,
			MaxKNumber::new
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

		return Math.max(a, b);
	}
}
