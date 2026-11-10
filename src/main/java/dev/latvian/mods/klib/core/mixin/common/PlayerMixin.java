package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.common.KLibPlayer;
import dev.latvian.mods.klib.entity.PlayerTarget;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin implements KLibPlayer, PlayerTarget.Container {
	@Unique
	private final PlayerTarget klib$playerTarget = new PlayerTarget.OfPlayer((Player) (Object) this);

	@Override
	public PlayerTarget klib$getPlayerTarget() {
		return klib$playerTarget;
	}
}
