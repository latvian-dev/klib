package dev.latvian.mods.klib.entity.number;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.PrefersDirectCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.Entity;

public record FixedEntityNumber(double number) implements EntityNumber, PrefersDirectCodec {
	public static final DynamicType<RegistryFriendlyByteBuf, EntityNumber> TYPE = DynamicType.create(
		"fixed",
		"number",
		Codec.DOUBLE,
		ByteBufCodecs.DOUBLE,
		FixedEntityNumber::new,
		FixedEntityNumber::number
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityNumber> type() {
		return TYPE;
	}

	@Override
	public double applyAsDouble(Entity entity) {
		return number;
	}
}
