package dev.latvian.mods.klib.registry;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public final class RegistryKeys<T> {
	public static <T> RegistryKeys<T> createKeys(Identifier id, String commonNamespace) {
		return new RegistryKeys<>(ResourceKey.createRegistryKey(id), commonNamespace);
	}

	public static <T> RegistryKeys<T> createKeys(Identifier id) {
		return createKeys(id, "minecraft");
	}

	private final ResourceKey<? extends Registry<T>> root;
	private final Codec<ResourceKey<T>> codec;
	private final StreamCodec<ByteBuf, ResourceKey<T>> streamCodec;

	private RegistryKeys(ResourceKey<? extends Registry<T>> root, String commonNamespace) {
		this.root = root;
		this.codec = KLibCodecs.commonResourceKey(root, commonNamespace);
		this.streamCodec = KLibStreamCodecs.commonResourceKey(root, commonNamespace);
	}

	public ResourceKey<T> create(Identifier id) {
		return ResourceKey.create(root, id);
	}

	public ResourceKey<? extends Registry<T>> root() {
		return root;
	}

	public Codec<ResourceKey<T>> codec() {
		return codec;
	}

	public StreamCodec<ByteBuf, ResourceKey<T>> streamCodec() {
		return streamCodec;
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
}
