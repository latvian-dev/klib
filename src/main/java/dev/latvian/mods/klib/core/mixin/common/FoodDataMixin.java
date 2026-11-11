package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.common.KLibFoodData;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FoodData.class)
public abstract class FoodDataMixin implements KLibFoodData {
	@Override
	@Accessor("exhaustionLevel")
	public abstract float klib$getExhaustionLevel();

	@Override
	@Accessor("exhaustionLevel")
	public abstract void klib$setExhaustionLevel(float value);

	@Override
	@Accessor("tickTimer")
	public abstract int klib$getTickTimer();

	@Override
	@Accessor("tickTimer")
	public abstract void klib$setTickTimer(int value);
}
