package dev.latvian.mods.klib.registry;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMaps;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;

public class CustomRegistryValueNetworkMap<V> {
	public static final CustomRegistryValueNetworkMap<?> EMPTY = new CustomRegistryValueNetworkMap<>(0);

	public static <V> CustomRegistryValueNetworkMap<V> create(int size) {
		return size <= 0 ? (CustomRegistryValueNetworkMap) EMPTY : new CustomRegistryValueNetworkMap<>(size);
	}

	public final Int2ObjectMap<Ref<V>> rx;
	public final Reference2IntMap<String> tx;

	private CustomRegistryValueNetworkMap(int size) {
		this.rx = size <= 0 ? Int2ObjectMaps.emptyMap() : new Int2ObjectOpenHashMap<>(size);
		this.tx = size <= 0 ? Reference2IntMaps.emptyMap() : new Reference2IntOpenHashMap<>(size);
	}

	public int getValueIndex(String key) {
		return tx.getInt(key.intern());
	}
}
