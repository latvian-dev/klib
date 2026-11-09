package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.entity.ServerPlayerContainer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements ServerPlayerContainer {
	@Override
	public void forEachPlayer(Consumer<ServerPlayer> callback) {
		((MinecraftServer) (Object) this).getPlayerList().getPlayers().forEach(callback);
	}
}
