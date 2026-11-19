package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMaps;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import org.jetbrains.annotations.Nullable;

public class CustomRegistryTypeNetworkMap<B extends ByteBuf, V> {
	public static final CustomRegistryTypeNetworkMap<?, ?> EMPTY = new CustomRegistryTypeNetworkMap<>(0);

	public static <B extends ByteBuf, V> CustomRegistryTypeNetworkMap<B, V> create(int size) {
		return size <= 0 ? (CustomRegistryTypeNetworkMap) EMPTY : new CustomRegistryTypeNetworkMap<>(size);
	}

	public final Int2ObjectMap<CustomRegistryType<B, V>> rx;
	public final Reference2IntMap<CustomRegistryType<B, V>> tx;

	private CustomRegistryTypeNetworkMap(int size) {
		this.rx = size <= 0 ? Int2ObjectMaps.emptyMap() : new Int2ObjectOpenHashMap<>(size);
		this.tx = size <= 0 ? Reference2IntMaps.emptyMap() : new Reference2IntOpenHashMap<>(size);
	}

	public int getIndex(@Nullable CustomRegistryType<B, V> value) {
		return value != null ? tx.getInt(value) : 0;
	}
}
