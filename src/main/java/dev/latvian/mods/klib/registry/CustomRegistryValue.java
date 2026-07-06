package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;

public interface CustomRegistryValue<B extends ByteBuf, T> extends WithKey, RefOptimizer<T>, CustomRegistryOwnTypeProvider<B, T> {
	CustomRegistry<B, T> getRegistry();

	@Override
	default String optionalKey() {
		return getRegistry().getKey((T) this);
	}

	default Ref<T> ref() {
		return getRegistry().ref((T) this);
	}
}
