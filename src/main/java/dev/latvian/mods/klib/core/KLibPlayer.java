package dev.latvian.mods.klib.core;

import dev.latvian.mods.klib.math.Line;
import net.minecraft.world.entity.player.Player;

public interface KLibPlayer extends KLibLivingEntity {
	@Override
	default Player klib$self() {
		return (Player) this;
	}

	@Override
	default Line klib$ray(float delta) {
		return klib$ray(klib$self().blockInteractionRange(), delta);
	}
}
