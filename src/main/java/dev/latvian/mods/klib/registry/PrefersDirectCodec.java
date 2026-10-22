package dev.latvian.mods.klib.registry;

import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.codec.KLibCodecErrors;

public interface PrefersDirectCodec {
	DataResult<String> ERROR = KLibCodecErrors.error("Prefers direct codec");

	default boolean getPrefersDirectCodec() {
		return true;
	}
}
