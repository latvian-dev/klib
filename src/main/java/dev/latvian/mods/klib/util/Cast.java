package dev.latvian.mods.klib.util;

public interface Cast {
	@SuppressWarnings("unchecked")
	static <T> T to(Object o) {
		return (T) o;
	}
}
