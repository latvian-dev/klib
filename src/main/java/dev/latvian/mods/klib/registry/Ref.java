package dev.latvian.mods.klib.registry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.klib.codec.KLibCodecErrors;
import dev.latvian.mods.klib.core.KLibFriendlyByteBuf;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public sealed interface Ref<V> extends WithKey, WithRef<V>, WithValue<V> permits UnitType, RefOfKey, RefOfValue {
	static <V> MapCodec<Ref<V>> contextRefCodec() {
		DataResult<Ref<V>> error = KLibCodecErrors.error("Could not retrieve ref from this context");

		return ExtraCodecs.retrieveContext(ops -> {
			if (ops instanceof RefOps<?, ?> refOps) {
				//noinspection unchecked
				return DataResult.success((Ref<V>) refOps.ref);
			}

			return error;
		});
	}

	static <B extends ByteBuf, V> StreamCodec<B, Ref<V>> contextRefStreamCodec() {
		return new StreamCodec<>() {
			@Override
			public Ref<V> decode(B buf) {
				var key = Utf8String.read(buf, Short.MAX_VALUE);

				if (key.isEmpty()) {
					throw new NullPointerException("Empty key");
				}

				if (!(buf instanceof FriendlyByteBuf friendlyBuf)) {
					throw new IllegalArgumentException("Expected FriendlyByteBuf but got " + buf.getClass());
				}

				var value = KLibFriendlyByteBuf.get(friendlyBuf);

				if (value instanceof Ref<?> ref) {
					return (Ref<V>) ref;
				} else if (value instanceof CustomRegistry<?, ?> customRegistry) {
					return (Ref<V>) customRegistry.ref(key);
				} else {
					throw new NullPointerException("Could not retrieve ref from this context");
				}
			}

			@Override
			public void encode(B buf, Ref<V> value) {
				Utf8String.write(buf, value.key(), Short.MAX_VALUE);
			}
		};
	}

	static <V> MapCodec<Optional<Ref<V>>> optionalContextRefCodec() {
		DataResult<Optional<Ref<V>>> fallback = DataResult.success(Optional.empty());

		return ExtraCodecs.retrieveContext(ops -> {
			if (ops instanceof RefOps<?, ?> refOps) {
				//noinspection unchecked
				return DataResult.success(Optional.of((Ref<V>) refOps.ref));
			}

			return fallback;
		});
	}

	static <B extends ByteBuf, V> StreamCodec<B, Optional<Ref<V>>> optionalContextRefStreamCodec() {
		return new StreamCodec<>() {
			@Override
			public Optional<Ref<V>> decode(B buf) {
				var key = Utf8String.read(buf, Short.MAX_VALUE);

				if (key.isEmpty()) {
					return Optional.empty();
				}

				if (!(buf instanceof FriendlyByteBuf friendlyBuf)) {
					return Optional.empty();
				}

				var value = KLibFriendlyByteBuf.get(friendlyBuf);

				if (value instanceof Ref<?> ref) {
					return Optional.of((Ref<V>) ref);
				} else if (value instanceof CustomRegistry<?, ?> customRegistry) {
					return Optional.of((Ref<V>) customRegistry.ref(key));
				} else {
					return Optional.empty();
				}
			}

			@Override
			public void encode(B buf, Optional<Ref<V>> value) {
				if (value.isEmpty()) {
					Utf8String.write(buf, "", Short.MAX_VALUE);
				} else {
					Utf8String.write(buf, value.get().key(), Short.MAX_VALUE);
				}
			}
		};
	}

	@Override
	default Ref<V> ref() {
		return this;
	}

	@Override
	default V value() {
		var value = optionalValue();

		if (value == null) {
			var key = key();
			throw new NullPointerException("Value of " + key + " isn't bound");
		}

		return value;
	}
}
