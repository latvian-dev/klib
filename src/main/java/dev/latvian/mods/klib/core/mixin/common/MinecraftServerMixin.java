package dev.latvian.mods.klib.core.mixin.common;

import dev.latvian.mods.klib.entity.PlayerTarget;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements PlayerTarget.Container {
	@Shadow
	@Final
	private Map<ResourceKey<Level>, ServerLevel> levels;

	@Unique
	private ServerLevel klib$overworld;

	@Unique
	private final PlayerTarget klib$playerTarget = new PlayerTarget.OfServer((MinecraftServer) (Object) this);

	@Inject(method = "createLevels", at = @At("RETURN"))
	private void klib$createLevels(CallbackInfo ci) {
		klib$overworld = levels.get(Level.OVERWORLD);
	}

	/**
	 * @author Lat
	 * @reason Optimization
	 */
	@Overwrite
	public final ServerLevel overworld() {
		return klib$overworld;
	}

	@Override
	public PlayerTarget klib$getPlayerTarget() {
		return klib$playerTarget;
	}
}
