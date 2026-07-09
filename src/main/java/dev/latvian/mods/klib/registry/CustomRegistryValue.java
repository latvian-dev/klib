package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;

public interface CustomRegistryValue<B extends ByteBuf, T> extends WithKey, WithRef<T>, RefOptimizer<T>, CustomRegistryOwnTypeProvider<B, T> {
	CustomRegistry<B, T> getRegistry();

	@Override
	default String optionalKey() {
		return getRegistry().getKey((T) this);
	}

	@Override
	default Ref<T> ref() {
		var type = type();
		return type instanceof UnitType<?, ?> ? (UnitType<B, T>) type : getRegistry().valueRef((T) this);
	}
}
