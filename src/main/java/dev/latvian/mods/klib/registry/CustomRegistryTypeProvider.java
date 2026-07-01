package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@FunctionalInterface
public interface CustomRegistryTypeProvider<B extends ByteBuf, T> extends Function<T, @Nullable CustomRegistryType<B, T>> {
	@Override
	@Nullable
	CustomRegistryType<B, T> apply(T value);
}
