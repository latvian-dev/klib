package dev.latvian.mods.klib.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class NeoKLibByteBuf extends RegistryFriendlyByteBuf implements KLibByteBuf {
	private Object specialValue;

	public NeoKLibByteBuf(ByteBuf source, RegistryAccess registryAccess, ConnectionType connectionType) {
		super(source, registryAccess, connectionType);
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
