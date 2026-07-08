package dev.latvian.mods.klib.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.util.Cast;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class DynamicType<B extends ByteBuf, V> extends CustomRegistryType<B, V> {
	public static <B extends ByteBuf, V, D extends V> DynamicType<B, V> create(String id, MapCodec<D> codec, StreamCodec<? super B, D> streamCodec) {
		return new DynamicType<>(id.intern(), Cast.to(codec), Cast.to(streamCodec));
	}

	public static <B extends ByteBuf, V, D extends V, T> DynamicType<B, V> create(String id, String fieldName, Codec<T> codec, StreamCodec<? super B, T> streamCodec, Function<T, D> factory, Function<D, T> getter) {
		return create(id, RecordCodecBuilder.mapCodec(instance -> instance.group(codec.fieldOf(fieldName).forGetter(getter)).apply(instance, factory)), streamCodec.map(factory, getter));
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