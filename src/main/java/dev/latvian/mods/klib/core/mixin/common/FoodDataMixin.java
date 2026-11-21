package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.KLibObject;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FoodData.class)
public abstract class FoodDataMixin implements KLibObject {
	@Shadow
	private float exhaustionLevel;

	@Shadow
	private int tickTimer;

	@Override
	public Object klib$get(int id) {
		return switch (id) {
			case 0 -> exhaustionLevel;
			case 1 -> tickTimer;
			default -> null;
		};
	}

	@Override
	public void klib$set(int id, Object value) {
		switch (id) {
			case 0 -> exhaustionLevel = (Float) value;
			case 1 -> tickTimer = (Integer) value;
		}
	}
}
