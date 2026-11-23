package dev.latvian.mods.klib.core.mixin.client;

import dev.latvian.mods.klib.core.api.client.KLibLocalPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin implements KLibLocalPlayer {
}
