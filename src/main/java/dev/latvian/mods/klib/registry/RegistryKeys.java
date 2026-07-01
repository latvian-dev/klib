package dev.latvian.mods.klib.registry;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.util.ID;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RegistryKeys<T> {
	private record Key<T>(ResourceKey<? extends Registry<T>> root, String commonNamespace) {
		@Override
		public boolean equals(Object obj) {
			return obj instanceof Key k && root == k.root && commonNamespace.equals(k.commonNamespace);
		}
	}

	private static final Map<Key<?>, RegistryKeys<?>> KEY_MAP = new ConcurrentHashMap<>();

	public static <T> RegistryKeys<T> createKeys(Identifier id, String commonNamespace) {
		return (RegistryKeys<T>) KEY_MAP.computeIfAbsent(new Key(ResourceKey.createRegistryKey(id), commonNamespace), RegistryKeys::new);
	}

	public static <T> RegistryKeys<T> createKeys(Identifier id) {
		return createKeys(id, "minecraft");
	}

	public static <T> RegistryKeys<T> read(ByteBuf buf) {
		var registry = ID.STREAM_CODEC.decode(buf);
		var commonNamespace = ByteBufCodecs.STRING_UTF8.decode(buf);
		return RegistryKeys.createKeys(registry, commonNamespace);
	}

	public static void write(ByteBuf buf, RegistryKeys<?> registryKeys) {
		ID.STREAM_CODEC.encode(buf, registryKeys.root.identifier());
		ByteBufCodecs.STRING_UTF8.encode(buf, registryKeys.commonNamespace);
	}

	private final ResourceKey<? extends Registry<T>> root;
	private final String commonNamespace;
	private final Codec<ResourceKey<T>> codec;
	private final StreamCodec<ByteBuf, ResourceKey<T>> streamCodec;

	private RegistryKeys(Key<T> key) {
		this.root = key.root;
		this.commonNamespace = key.commonNamespace;
		this.codec = KLibCodecs.commonResourceKey(root, commonNamespace);
		this.streamCodec = KLibStreamCodecs.commonResourceKey(root, commonNamespace);
	}

	public ResourceKey<T> create(Identifier id) {
		return ResourceKey.create(root, id);
	}

	public ResourceKey<? extends Registry<T>> root() {
		return root;
	}

	public String commonNamespace() {
		return commonNamespace;
	}

	public Codec<ResourceKey<T>> codec() {
		return codec;
	}

	public StreamCodec<ByteBuf, ResourceKey<T>> streamCodec() {
		return streamCodec;
	}

	public DataType<ResourceKey<T>> dataType() {
		return DataType.of(codec, streamCodec);
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof RegistryKeys<?> other && root == other.root;
	}

	@Override
	public int hashCode() {
		return root.hashCode();
	}

	@Override
	public String toString() {
		return "RegistryKeys[" + root.identifier() + ']';
	}

	public String encode(ResourceKey<T> key) {
		return encode(key.identifier());
	}

	public String encode(Identifier id) {
		return id.getNamespace().equals(commonNamespace) ? id.getPath() : id.toString();
	}
}
