package dev.latvian.mods.klib.codec;

import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.util.Cast;

public interface KLibCodecErrors {
	static <T> DataResult<T> error(String message) {
		return DataResult.error(() -> message);
	}

	static <T> DataResult<T> error(Throwable ex) {
		return DataResult.error(ex::getMessage);
	}

	DataResult<?> VALUE_IS_NULL = error("Value is null");
	DataResult<?> VALUE_IS_EMPTY = error("Value is empty");
	DataResult<?> LIST_IS_NULL = error("List is null");
	DataResult<?> LIST_IS_EMPTY = error("List is empty");
	DataResult<?> MAP_IS_NULL = error("Map is null");
	DataResult<?> MAP_IS_EMPTY = error("Map is empty");

	static <T> DataResult<T> valueIsNull() {
		return Cast.to(VALUE_IS_NULL);
	}

	static <T> DataResult<T> valueIsEmpty() {
		return Cast.to(VALUE_IS_EMPTY);
	}

	static <T> DataResult<T> listIsNull() {
		return Cast.to(LIST_IS_NULL);
	}

	static <T> DataResult<T> listIsEmpty() {
		return Cast.to(LIST_IS_EMPTY);
	}

	static <T> DataResult<T> mapIsNull() {
		return Cast.to(MAP_IS_NULL);
	}

	static <T> DataResult<T> mapIsEmpty() {
		return Cast.to(MAP_IS_EMPTY);
	}
}
