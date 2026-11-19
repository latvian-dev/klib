package dev.latvian.mods.klib.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.OptionalDefaultStreamCodec;
import dev.latvian.mods.klib.util.Cast;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public final class DynamicType<B extends ByteBuf, V> extends CustomRegistryType<B, V> {
	public static <B extends ByteBuf, V, D extends V> DynamicType<B, V> create(String id, MapCodec<D> mapCodec, StreamCodec<? super B, D> streamCodec) {
		return new DynamicType<>(id.intern(), Cast.to(mapCodec), Cast.to(streamCodec));
	}

	public static <B extends ByteBuf, V, D extends V, T> DynamicType<B, V> create(String id, String fieldName, Codec<T> codec, StreamCodec<? super B, T> streamCodec, Function<T, D> factory, Function<D, T> getter) {
		MapCodec<D> dCodec = RecordCodecBuilder.mapCodec(instance -> instance.group(codec.fieldOf(fieldName).forGetter(getter)).apply(instance, factory));
		StreamCodec<? super B, D> dStreamCodec = streamCodec.map(factory, getter);
		return create(id, dCodec, dStreamCodec);
	}

	public static <B extends ByteBuf, V, D extends V, T> DynamicType<B, V> create(String id, String fieldName, Codec<T> codec, StreamCodec<? super B, T> streamCodec, Function<T, D> factory, Function<D, T> getter, T defaultValue) {
		MapCodec<D> dCodec = RecordCodecBuilder.mapCodec(instance -> instance.group(codec.optionalFieldOf(fieldName, defaultValue).forGetter(getter)).apply(instance, factory));
		StreamCodec<? super B, D> dStreamCodec = new OptionalDefaultStreamCodec<>(streamCodec, defaultValue).map(factory, getter);
		return create(id, dCodec, dStreamCodec);
	}

	private static final AtomicInteger PROPERTY_ID_FACTORY = new AtomicInteger(0);

	public static int newPropertyId() {
		return PROPERTY_ID_FACTORY.incrementAndGet();
	}

	private Int2ObjectMap<Object> properties;

	private DynamicType(String key, MapCodec<V> mapCodec, StreamCodec<? super B, V> streamCodec) {
		super(key);
		this.mapCodec = mapCodec;
		this.streamCodec = streamCodec;
	}

	public DynamicType<B, V> version(int version) {
		this.version = version;
		return this;
	}

	public DynamicType<B, V> backwardsCompatibility(int version, StreamCodec<? super B, V> streamCodec) {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public DynamicType<B, V> type() {
		return this;
	}

	public void setProperty(int key, @Nullable Object value) {
		if (value == null) {
			if (properties != null) {
				properties.remove(key);

				if (properties.isEmpty()) {
					properties = null;
				}
			}
		} else {
			if (properties == null) {
				properties = new Int2ObjectArrayMap<>(3);
			}

			properties.put(key, value);
		}
	}

	@Nullable
	public Object getProperty(int key) {
		return properties == null ? null : properties.get(key);
	}
}