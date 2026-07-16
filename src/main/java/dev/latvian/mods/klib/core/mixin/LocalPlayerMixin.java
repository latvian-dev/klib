package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibLocalPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements KLibLocalPlayer {
}
