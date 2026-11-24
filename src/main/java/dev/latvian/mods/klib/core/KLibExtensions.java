package dev.latvian.mods.klib.core;

import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.KLibBuiltInRegistries;
import dev.latvian.mods.klib.core.api.KLibObject;
import dev.latvian.mods.klib.interpolation.WrappedEasingType;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class KLibExtensions {
	public static MethodHandle getMethodHandle;
	public static MethodHandle setMethodHandle;

	private static Class<?> findClass(String name) throws ClassNotFoundException {
		var classLoader = KLibExtensions.class.getClassLoader();
		ClassLoader previousClassLoader = null;

		while (classLoader != null && classLoader != previousClassLoader) {
			try {
				return Class.forName(name, true, classLoader);
			} catch (Throwable ignored) {
				previousClassLoader = classLoader;
				classLoader = classLoader.getParent();
			}
		}

		throw new ClassNotFoundException(name);
	}

	public static void init() {
		try {
			var lookup = MethodHandles.lookup();

			var c = findClass("dev.latvian.mods.klib.core.api.KLibObject");
			getMethodHandle = lookup.findVirtual(c, "klib$get", MethodType.methodType(Object.class, int.class));
			setMethodHandle = lookup.findVirtual(c, "klib$set", MethodType.methodType(void.class, int.class, Object.class));

			var b = findClass("dev.latvian.mods.klib.core.api.KLibBridge");
			lookup.findStaticVarHandle(b, "CONFIGURATION_TASKS", List.class).set(KLibBuiltInRegistries.CONFIGURATION_TASKS);
			lookup.findStaticVarHandle(b, "EASING_TYPE_CODEC", UnaryOperator.class).set(WrappedEasingType.wrap());
			KLib.LOGGER.info("KLibExtensions initialized");
		} catch (Throwable ex) {
			throw new RuntimeException("Couldn't load KLibExtensions", ex);
		}
	}

	public static Object getRaw(Object object, int id) {
		if (object instanceof KLibObject ext) {
			return ext.klib$get(id);
		} else if (object == null) {
			throw new NullPointerException("Can't get property " + id + ", object is null");
		}

		try {
			return getMethodHandle.invoke(object, id);
		} catch (Throwable ex) {
			throw new RuntimeException("Failed to invoke KLibObject#klib$get", ex);
		}
	}

	public static <T> T get(Object object, int id) {
		return (T) getRaw(object, id);
	}

	public static void set(Object object, int id, Object value) {
		if (object instanceof KLibObject ext) {
			ext.klib$set(id, value);
			return;
		} else if (object == null) {
			throw new NullPointerException("Can't set property " + id + ", object is null");
		}

		try {
			setMethodHandle.invoke(object, id, value);
		} catch (Throwable ex) {
			throw new RuntimeException("Failed to invoke KLibObject#klib$set", ex);
		}
	}

	public static <K, V> V compute(K object, int id, Function<K, V> factory) {
		var value = getRaw(object, id);

		if (value == null) {
			value = factory.apply(object);
			set(object, id, value);
		}

		return (V) value;
	}
}
