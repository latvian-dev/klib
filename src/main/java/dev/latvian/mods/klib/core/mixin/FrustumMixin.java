package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibFrustum;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Frustum.class)
public abstract class FrustumMixin implements KLibFrustum {
}
