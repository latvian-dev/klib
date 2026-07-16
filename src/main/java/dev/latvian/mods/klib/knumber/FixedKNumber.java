package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public record FixedKNumber(@Nullable UnitType<RegistryFriendlyByteBuf, KNumber> typeOverride, Double number) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"fixed",
		"number",
		Codec.DOUBLE,
		ByteBufCodecs.DOUBLE,
		FixedKNumber::new,
		FixedKNumber::number
	);

	public FixedKNumber(double value) {
		this(null, value);
	}

	@Override
	public CustomRegistryType<RegistryFriendlyByteBuf, KNumber> type() {
		return typeOverride == null ? TYPE : typeOverride;
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
	public @NonNull String toString() {
		return number.toString();
	}
}
