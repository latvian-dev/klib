package dev.latvian.mods.klib.core.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.core.api.KLibBridge;
import net.minecraft.util.EasingType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EasingType.class)
public interface EasingTypeMixin {
	@ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;xmap(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
	private static Codec<EasingType> klib$codec(Codec<EasingType> original) {
		return Codec.lazyInitialized(() -> KLibBridge.EASING_TYPE_CODEC.apply(original));
	}
}
