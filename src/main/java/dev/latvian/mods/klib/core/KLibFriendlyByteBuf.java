package dev.latvian.mods.klib.core;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public interface KLibFriendlyByteBuf {
	static void set(FriendlyByteBuf buf, @Nullable Object value) {
		((KLibFriendlyByteBuf) buf).klib$setSpecialValue(value);
	}

	@Nullable
	static Object get(FriendlyByteBuf buf) {
		return ((KLibFriendlyByteBuf) buf).klib$getSpecialValue();
	}

	default void klib$setSpecialValue(Object value) {
		throw new NoMixinException(this);
	}

	@Nullable
	default Object klib$getSpecialValue() {
		throw new NoMixinException(this);
	}
}
