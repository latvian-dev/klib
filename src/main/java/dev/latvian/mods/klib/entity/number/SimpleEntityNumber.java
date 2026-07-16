package dev.latvian.mods.klib.entity.number;

import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;

import java.util.function.ToDoubleFunction;

public record SimpleEntityNumber(UnitType<RegistryFriendlyByteBuf, EntityNumber> type, ToDoubleFunction<Entity> function) implements EntityNumber {
	@Override
	public double applyAsDouble(Entity entity) {
		return function.applyAsDouble(entity);
	}

	@Override
	public String toString() {
		return type().toString();
	}
}
