package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.common.KLibLevel;
import dev.latvian.mods.klib.entity.PlayerTarget;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Level.class)
public abstract class LevelMixin implements KLibLevel, PlayerTarget.Container {
	@Unique
	private final PlayerTarget klib$playerTarget = new PlayerTarget.OfLevel((Level) (Object) this);

	@Override
	public PlayerTarget klib$getPlayerTarget() {
		return klib$playerTarget;
	}
}
