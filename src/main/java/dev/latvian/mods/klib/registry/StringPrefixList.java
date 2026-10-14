package dev.latvian.mods.klib.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.codec.KLibCodecErrors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringPrefixList<T> {
	private final Predicate<T> isStringLiteral;
	private final DataResult<T> errorMissingPrefix = KLibCodecErrors.error("Missing prefix");
	private final DataResult<String> errorNotALiteral = KLibCodecErrors.error("Not a literal");
	private final List<Map.Entry<String, Function<String, DataResult<T>>>> handlers = new ArrayList<>();
	private final Codec<T> codec;

	public StringPrefixList(Predicate<T> isStringLiteral) {
		this.isStringLiteral = isStringLiteral;
		this.codec = Codec.STRING.flatXmap(input -> {
			for (var entry : handlers) {
				if (input.startsWith(entry.getKey())) {
					return entry.getValue().apply(input);
				}
			}

			return errorMissingPrefix;
		}, value -> {
			if (this.isStringLiteral.test(value)) {
				return DataResult.success(value.toString());
			} else {
				return errorNotALiteral;
			}
		});
	}

	public void add(String prefix, Function<String, DataResult<T>> function) {
		handlers.add(Map.entry(prefix, function));
	}

	public void addSimple(String prefix, Function<String, T> function) {
		int len = prefix.length();
		add(prefix, input -> DataResult.success(function.apply(input.substring(len))));
	}

	public Codec<T> codec() {
		return codec;
	}
}
