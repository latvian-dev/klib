package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibServerLevel;
import dev.latvian.mods.klib.entity.ServerPlayerContainer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements KLibServerLevel, ServerPlayerContainer {
	@Override
	public void forEachPlayer(Consumer<ServerPlayer> callback) {
		((ServerLevel) (Object) this).players().forEach(callback);
	}
}
