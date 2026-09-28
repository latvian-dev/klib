package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin implements KLibPlayer {
}
