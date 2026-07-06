package dev.latvian.mods.klib.registry;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.klib.util.Cast;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class DynamicType<B extends ByteBuf, V> extends CustomRegistryType<B, V> {
	public static <B extends ByteBuf, V, D extends V> DynamicType<B, V> create(String id, MapCodec<D> codec, StreamCodec<? super B, D> streamCodec) {
		return new DynamicType<>(id.intern(), Cast.to(codec), Cast.to(streamCodec));
	}

	DynamicType(String key, MapCodec<V> codec, StreamCodec<? super B, V> streamCodec) {
		super(key);
		this.codec = codec;
		this.streamCodec = streamCodec;
	}

	public DynamicType<B, V> version(int version) {
		this.version = version;
		return this;
	}

	public DynamicType<B, V> backwardsCompatibility(int version, StreamCodec<? super B, V> streamCodec) {
		throw new IllegalStateException("Not yet implemented");
	}
}