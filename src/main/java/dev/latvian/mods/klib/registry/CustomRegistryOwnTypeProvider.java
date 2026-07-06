package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

public interface CustomRegistryOwnTypeProvider<B extends ByteBuf, T> {
	@Nullable
	default CustomRegistryType<B, T> type() {
		return null;
	}
}
