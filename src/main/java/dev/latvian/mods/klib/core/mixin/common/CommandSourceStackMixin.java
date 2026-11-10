package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.entity.PlayerTarget;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CommandSourceStack.class)
public abstract class CommandSourceStackMixin implements PlayerTarget.Container {
	@Unique
	private final PlayerTarget klib$playerTarget = new PlayerTarget.OfCommandSourceStack((CommandSourceStack) (Object) this);

	@Override
	public PlayerTarget klib$getPlayerTarget() {
		return klib$playerTarget;
	}
}
