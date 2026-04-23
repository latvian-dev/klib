package dev.latvian.mods.klib.core.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import dev.latvian.mods.klib.math.ClientMatrices;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ProjectionMatrixBuffer;getBuffer(Lorg/joml/Matrix4f;)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"))
	private GpuBufferSlice klib$setProjectionMatrix(ProjectionMatrixBuffer instance, Matrix4f projectionMatrix) {
		ClientMatrices.PERSPECTIVE.set(projectionMatrix);
		// ClientMatrices.FRUSTUM.set(fMatrix);
		return instance.getBuffer(projectionMatrix);
	}
}
