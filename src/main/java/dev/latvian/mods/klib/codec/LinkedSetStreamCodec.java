package dev.latvian.mods.klib.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;

import java.util.LinkedHashSet;
import java.util.Set;

public record LinkedSetStreamCodec<B, V>(StreamCodec<? super B, V> parent) implements StreamCodec<B, Set<V>> {
	@Override
	public Set<V> decode(B buffer) {
		int size = VarInt.read((ByteBuf) buffer);

		if (size == 0) {
			return Set.of();
		} else if (size == 1) {
			return Set.of(parent.decode(buffer));
		} else {
			var set = new LinkedHashSet<V>(size);

			for (int i = 0; i < size; i++) {
				set.add(parent.decode(buffer));
			}

			return set;
		}
	}

	@Override
	public void encode(B buffer, Set<V> value) {
		VarInt.write((ByteBuf) buffer, value.size());

		for (V v : value) {
			parent.encode(buffer, v);
		}
	}
}
