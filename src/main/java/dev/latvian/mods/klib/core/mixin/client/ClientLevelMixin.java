package dev.latvian.mods.klib.core.mixin.client;

import dev.latvian.mods.klib.core.api.client.KLibClientLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements KLibClientLevel {
}
