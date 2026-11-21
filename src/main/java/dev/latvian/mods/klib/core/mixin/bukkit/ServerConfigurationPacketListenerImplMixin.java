package dev.latvian.mods.klib.core.mixin.bukkit;

import dev.latvian.mods.klib.core.api.KLibBridge;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public class ServerConfigurationPacketListenerImplMixin {
	@Shadow
	@Final
	private Queue<ConfigurationTask> configurationTasks;

	@Inject(method = "addOptionalTasks", at = @At("RETURN"))
	private void vl$addOptionalTasks(CallbackInfo ci) {
		for (var task : KLibBridge.CONFIGURATION_TASKS) {
			configurationTasks.add(task.get());
		}
	}
}
