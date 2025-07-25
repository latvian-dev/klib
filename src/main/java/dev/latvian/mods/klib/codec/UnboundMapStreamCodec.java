package dev.latvian.mods.klib.codec;

import dev.latvian.mods.klib.util.MapFactory;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;

import java.util.Map;

public record UnboundMapStreamCodec<B extends ByteBuf, K, V>(StreamCodec<? super B, K> keyCodec, StreamCodec<? super B, V> valueCodec, boolean ordered, boolean identity) implements StreamCodec<B, Map<K, V>> {
	@Override
	public Map<K, V> decode(B buf) {
		int size = VarInt.read(buf);

		if (size == 0) {
			return Map.of();
		} else if (size == 1) {
			return Map.of(keyCodec.decode(buf), valueCodec.decode(buf));
		} else {
			Map<K, V> map = MapFactory.create(size, ordered, identity);

			for (int i = 0; i < size; i++) {
				map.put(keyCodec.decode(buf), valueCodec.decode(buf));
			}

			return map;
		}
	}

	@Override
	public void encode(B buf, Map<K, V> value) {
		VarInt.write(buf, value.size());

		if (!value.isEmpty()) {
			for (var entry : value.entrySet()) {
				keyCodec.encode(buf, entry.getKey());
				valueCodec.encode(buf, entry.getValue());
			}
		}
	}
}
