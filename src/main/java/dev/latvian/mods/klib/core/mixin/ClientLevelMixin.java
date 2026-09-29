package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibClientLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements KLibClientLevel {
	@Shadow
	private boolean tickDayTime;

	@Override
	public boolean klib$getTickDayTime() {
		return tickDayTime;
	}
}
