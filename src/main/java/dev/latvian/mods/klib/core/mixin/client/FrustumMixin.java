package dev.latvian.mods.klib.core.mixin.client;

import dev.latvian.mods.klib.core.api.client.KLibFrustum;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Frustum.class)
public abstract class FrustumMixin implements KLibFrustum {
}
