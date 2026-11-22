package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.core.api.KLibObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerCommonPacketListenerImplMixin implements KLibObject {
	@Shadow
	@Final
	protected MinecraftServer server;

	@Override
	public Object klib$get(int id) {
		return id == 0 ? server : null;
	}
}
