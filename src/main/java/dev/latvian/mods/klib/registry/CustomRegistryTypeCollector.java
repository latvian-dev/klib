package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.Identifier;

public interface CustomRegistryTypeCollector<B extends ByteBuf, T> {
	void register(CustomRegistryType<B, T> type);

	void register(Identifier id, T unit);
}
