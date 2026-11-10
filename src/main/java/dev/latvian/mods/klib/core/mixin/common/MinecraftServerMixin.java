package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.entity.PlayerTarget;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements PlayerTarget.Container {
	@Unique
	private final PlayerTarget klib$playerTarget = new PlayerTarget.OfServer((MinecraftServer) (Object) this);

	@Override
	public PlayerTarget klib$getPlayerTarget() {
		return klib$playerTarget;
	}
}
