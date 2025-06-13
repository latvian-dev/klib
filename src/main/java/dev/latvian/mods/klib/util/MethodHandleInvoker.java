package dev.latvian.mods.klib.util;

import java.lang.invoke.MethodHandle;
import java.util.function.Function;

public record MethodHandleInvoker<C>(MethodHandle methodHandle) implements Function<Object[], C> {
	@Override
	public C apply(Object[] objects) {
		try {
			return (C) methodHandle.invokeWithArguments(objects);
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
	}
}
