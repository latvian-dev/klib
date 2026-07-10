package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibFriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin implements KLibFriendlyByteBuf {
	@Unique
	private Object klib$specialValue;

	@Override
	public void klib$setSpecialValue(Object value) {
		klib$specialValue = value;
	}

	@Override
	@Nullable
	public Object klib$getSpecialValue() {
		return klib$specialValue;
	}
}
