package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibClientLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements KLibClientLevel {
}
