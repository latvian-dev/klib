package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.entity.ServerPlayerContainer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements ServerPlayerContainer {
	@Override
	public void forEachPlayer(Consumer<ServerPlayer> callback) {
		callback.accept((ServerPlayer) (Object) this);
	}
}
