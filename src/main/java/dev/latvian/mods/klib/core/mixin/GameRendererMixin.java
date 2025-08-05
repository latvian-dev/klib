package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.math.ClientMatrices;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;prepareCullFrustum(Lnet/minecraft/world/phys/Vec3;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"))
	private void klib$getFrustumMatrix(LevelRenderer instance, Vec3 cameraPosition, Matrix4f fMatrix, Matrix4f projectionMatrix) {
		ClientMatrices.PERSPECTIVE.set(projectionMatrix);
		instance.prepareCullFrustum(cameraPosition, fMatrix, projectionMatrix);
		ClientMatrices.FRUSTUM.set(fMatrix);
	}
}
