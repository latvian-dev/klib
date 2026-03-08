package dev.latvian.mods.klib.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import dev.latvian.mods.klib.util.Cast;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class FlatMapCodec<K, H> extends MapCodec<H> {
	private final String keyName;
	private final String valueName;
	private final Codec<K> keyCodec;
	private final Function<K, Codec<?>> valueCodecGetter;
	private final Function<H, K> keyGetter;
	private final Function<H, Object> valueGetter;
	private final BiFunction<K, Object, H> factory;

	public FlatMapCodec(String keyName, String valueName, Codec<K> keyCodec, Function<K, Codec<?>> valueCodecGetter, Function<H, K> keyGetter, Function<H, Object> valueGetter, BiFunction<K, Object, H> factory) {
		this.keyName = keyName;
		this.valueName = valueName;
		this.keyCodec = keyCodec;
		this.valueCodecGetter = valueCodecGetter;
		this.keyGetter = keyGetter;
		this.valueGetter = valueGetter;
		this.factory = factory;
	}

	@Override
	public <O> Stream<O> keys(DynamicOps<O> ops) {
		return Stream.of(ops.createString(keyName), ops.createString(valueName));
	}

	@Override
	public <O> DataResult<H> decode(DynamicOps<O> ops, MapLike<O> input) {
		var decodedKey = input.get(keyName);

		if (decodedKey == null) {
			return DataResult.error(() -> "Input does not contain a key [" + keyName + "]: " + input);
		}

		var decodedValue = input.get(valueName);

		if (decodedValue == null) {
			return DataResult.error(() -> "Input does not contain a value [" + valueName + "]: " + input);
		}

		return keyCodec.decode(ops, decodedKey).flatMap(keyPair -> {
			var key = keyPair.getFirst();
			var valueCodec = valueCodecGetter.apply(key);

			// TODO: Compressed
			return valueCodec.decode(ops, decodedValue).flatMap(valuePair -> DataResult.success(factory.apply(key, valuePair.getFirst())));
		});
	}

	@Override
	public <O> RecordBuilder<O> encode(H input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
		var key = keyGetter.apply(input);
		var encodedKey = keyCodec.encodeStart(ops, key);

		if (encodedKey.isError()) {
			return prefix.withErrorsFrom(encodedKey);
		}

		var value = valueGetter.apply(input);
		var valueCodec = valueCodecGetter.apply(key);
		var encodedValue = valueCodec.encodeStart(ops, Cast.to(value));

		if (encodedValue.isError()) {
			return prefix.withErrorsFrom(encodedValue);
		}

		// TODO: Compressed
		return prefix.add(keyName, encodedKey).add(valueName, encodedValue);
	}
}
