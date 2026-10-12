package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibLocalPlayer;
import dev.latvian.mods.klib.math.Line;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements KLibLocalPlayer {
	@Shadow
	@Final
	protected Minecraft minecraft;

	@Override
	public Line klib$ray(double distance, float delta) {
		return minecraft.gameRenderer.getMainCamera().klib$ray(distance);
	}
}
