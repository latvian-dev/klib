package dev.latvian.mods.klib.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record ListStreamCodec<B, V>(StreamCodec<? super B, V> parent) implements StreamCodec<B, List<V>> {
	@Override
	public List<V> decode(B buffer) {
		int size = VarInt.read((ByteBuf) buffer);

		if (size == 0) {
			return List.of();
		} else if (size == 1) {
			return List.of(parent.decode(buffer));
		} else {
			var list = new ArrayList<V>(size);

			for (int i = 0; i < size; i++) {
				list.add(parent.decode(buffer));
			}

			return list;
		}
	}

	@Override
	public void encode(B buffer, List<V> value) {
		VarInt.write((ByteBuf) buffer, value.size());

		for (V v : value) {
			parent.encode(buffer, v);
		}
	}
}
