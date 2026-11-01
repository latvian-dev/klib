package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.PrefersDirectCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import org.jetbrains.annotations.NotNull;

public record FixedKNumber(Double number) implements KNumber, PrefersDirectCodec {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"fixed",
		"number",
		Codec.DOUBLE,
		ByteBufCodecs.DOUBLE,
		FixedKNumber::new,
		FixedKNumber::number
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KNumber> type() {
		return TYPE;
	}

	@Override
	public Double get(KNumberContext ctx) {
		return number;
	}

	@Override
	public KNumber offset(KNumber other) {
		if (other instanceof FixedKNumber n) {
			return KNumber.of(number + n.number);
		}

		return KNumber.super.offset(other);
	}

	@Override
	public KNumber scale(KNumber other) {
		if (other instanceof FixedKNumber n) {
			return KNumber.of(number * n.number);
		}

		return KNumber.super.scale(other);
	}

	@Override
	public @NotNull String toString() {
		return number.toString();
	}
}
