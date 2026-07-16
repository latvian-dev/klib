package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record OffsetKNumber(Ref<KNumber> a, Ref<KNumber> b) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"offset",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.fieldOf("a").forGetter(OffsetKNumber::a),
			KNumber.CODEC.fieldOf("b").forGetter(OffsetKNumber::b)
		).apply(instance, OffsetKNumber::new)),
		CompositeStreamCodec.of(
			KNumber.STREAM_CODEC, OffsetKNumber::a,
			KNumber.STREAM_CODEC, OffsetKNumber::b,
			OffsetKNumber::new
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

		return a + b;
	}
}
