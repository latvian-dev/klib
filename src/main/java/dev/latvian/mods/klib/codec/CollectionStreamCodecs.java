package dev.latvian.mods.klib.codec;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortLists;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

public interface CollectionStreamCodecs {
	StreamCodec<ByteBuf, IntList> VAR_INT_LIST = new StreamCodec<>() {
		@Override
		public IntList decode(ByteBuf buf) {
			int size = VarInt.read(buf);

			if (size == 0) {
				return IntLists.emptyList();
			} else if (size == 1) {
				return IntLists.singleton(VarInt.read(buf));
			} else {
				var list = new IntArrayList(size);

				for (int i = 0; i < size; i++) {
					list.add(VarInt.read(buf));
				}

				return list;
			}
		}

		@Override
		public void encode(ByteBuf buf, IntList value) {
			VarInt.write(buf, value.size());

			for (int i = 0; i < value.size(); i++) {
				VarInt.write(buf, value.getInt(i));
			}
		}
	};

	StreamCodec<ByteBuf, LongList> LONG_LIST = new StreamCodec<>() {
		@Override
		public LongList decode(ByteBuf buf) {
			int size = VarInt.read(buf);

			if (size == 0) {
				return LongLists.emptyList();
			} else if (size == 1) {
				return LongLists.singleton(buf.readLong());
			} else {
				var list = new LongArrayList(size);

				for (int i = 0; i < size; i++) {
					list.add(buf.readLong());
				}

				return list;
			}
		}

		@Override
		public void encode(ByteBuf buf, LongList value) {
			VarInt.write(buf, value.size());

			for (int i = 0; i < value.size(); i++) {
				buf.writeLong(value.getLong(i));
			}
		}
	};

	StreamCodec<ByteBuf, ShortList> SHORT_LIST = new StreamCodec<>() {
		@Override
		public ShortList decode(ByteBuf buf) {
			int size = VarInt.read(buf);

			if (size == 0) {
				return ShortLists.emptyList();
			} else if (size == 1) {
				return ShortLists.singleton((short) VarInt.read(buf));
			} else {
				var list = new ShortArrayList(size);

				for (int i = 0; i < size; i++) {
					list.add((short) VarInt.read(buf));
				}

				return list;
			}
		}

		@Override
		public void encode(ByteBuf buf, ShortList value) {
			VarInt.write(buf, value.size());

			for (int i = 0; i < value.size(); i++) {
				VarInt.write(buf, value.getShort(i));
			}
		}
	};

	static <B extends ByteBuf, K, V> StreamCodec<B, Map<K, V>> listToMap(StreamCodec<B, V> codec, Function<V, K> keyMapper) {
		return KLibStreamCodecs.listOf(codec).map(list -> CollectionCodecs.listToMap(list, keyMapper), map -> map.isEmpty() ? List.of() : new ArrayList<>(map.values()));
	}

	static <B extends ByteBuf, V> StreamCodec<B, Int2ObjectMap<V>> listToInt2ObjectMap(StreamCodec<B, V> codec, ToIntFunction<V> keyMapper) {
		return KLibStreamCodecs.listOf(codec).map(list -> CollectionCodecs.listToInt2ObjectMap(list, keyMapper), map -> map.isEmpty() ? List.of() : new ArrayList<>(map.values()));
	}
}
