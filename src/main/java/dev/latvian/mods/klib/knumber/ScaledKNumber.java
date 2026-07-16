package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record ScaledKNumber(Ref<KNumber> a, Ref<KNumber> b) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"scaled",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.fieldOf("a").forGetter(ScaledKNumber::a),
			KNumber.CODEC.fieldOf("b").forGetter(ScaledKNumber::b)
		).apply(instance, ScaledKNumber::new)),
		CompositeStreamCodec.of(
			KNumber.STREAM_CODEC, ScaledKNumber::a,
			KNumber.STREAM_CODEC, ScaledKNumber::b,
			ScaledKNumber::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KNumber> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Double get(KNumberContext ctx) {
		var a = this.a.value().get(ctx);
		var b = this.b.value().get(ctx);

		if (a == null || b == null) {
			return null;
		}

		return a * b;
	}
}
