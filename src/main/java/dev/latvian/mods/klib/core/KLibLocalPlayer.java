package dev.latvian.mods.klib.core;

import dev.latvian.mods.klib.math.Line;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public interface KLibLocalPlayer extends KLibPlayer {
	@Override
	default LocalPlayer klib$self() {
		return (LocalPlayer) this;
	}

	@Override
	default Line klib$ray(double distance, float delta) {
		return Minecraft.getInstance().gameRenderer.getMainCamera().klib$ray(distance);
	}
}
