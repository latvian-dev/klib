package dev.latvian.mods.klib.entity.number;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public record FixedEntityNumber(@Nullable UnitType<RegistryFriendlyByteBuf, EntityNumber> typeOverride, double number) implements EntityNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, EntityNumber> TYPE = DynamicType.create(
		"fixed",
		"number",
		Codec.DOUBLE,
		ByteBufCodecs.DOUBLE,
		FixedEntityNumber::new,
		FixedEntityNumber::number
	);

	public FixedEntityNumber(double number) {
		this(null, number);
	}

	@Override
	public CustomRegistryType<RegistryFriendlyByteBuf, EntityNumber> type() {
		return typeOverride == null ? TYPE : typeOverride;
	}

	@Override
	public double applyAsDouble(Entity entity) {
		return number;
	}
}
