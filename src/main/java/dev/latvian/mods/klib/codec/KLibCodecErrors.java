package dev.latvian.mods.klib.codec;

import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.knumber.KNumber;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.util.Cast;
import net.minecraft.world.phys.Vec3;

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
	DataResult<Boolean> NOT_A_BOOLEAN = KLibCodecErrors.error("Not a boolean");
	DataResult<String> NOT_A_STRING = KLibCodecErrors.error("Not a string");
	DataResult<Double> NOT_A_DOUBLE = KLibCodecErrors.error("Not a number");
	DataResult<Float> NOT_A_FLOAT = KLibCodecErrors.error("Not a number");
	DataResult<Vec3> NOT_A_VEC3 = KLibCodecErrors.error("Not a vector");
	DataResult<Ref<KNumber>> NOT_A_KNUMBER = KLibCodecErrors.error("Not a number");

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
