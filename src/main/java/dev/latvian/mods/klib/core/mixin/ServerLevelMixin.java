package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibServerLevel;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements KLibServerLevel {
}
