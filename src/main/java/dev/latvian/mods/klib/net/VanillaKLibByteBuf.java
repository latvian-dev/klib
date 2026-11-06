package dev.latvian.mods.klib.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class VanillaKLibByteBuf extends RegistryFriendlyByteBuf implements KLibByteBuf {
	private Object specialValue;

	public VanillaKLibByteBuf(ByteBuf source, RegistryAccess registryAccess) {
		super(source, registryAccess);
	}

	@Override
	public void setSpecialValue(@Nullable Object value) {
		specialValue = value;
	}

	@Override
	@Nullable
	public Object getSpecialValue() {
		return specialValue;
	}
}
