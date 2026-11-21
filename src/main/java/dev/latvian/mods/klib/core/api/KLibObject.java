package dev.latvian.mods.klib.core.api;

public interface KLibObject {
	default Object klib$get(int id) {
		return null;
	}

	default void klib$set(int id, Object value) {
	}
}
